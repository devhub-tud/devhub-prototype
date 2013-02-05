package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

import java.net.URL;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.jaxrs.api.projects.InviteException;
import nl.tudelft.ewi.dea.jaxrs.api.projects.InviteManager;
import nl.tudelft.ewi.dea.jaxrs.api.projects.services.ServicesBackend;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.devhub.services.ServiceException;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildIdentifier;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildProject;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryRepresentation;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.persist.Transactional;

@Slf4j
public class ProvisionTask implements Runnable {

	private static final Object groupAssigner = new Object();

	private final Provisioner provisioner;
	private final CourseDao courseDao;
	private final ProjectDao projectDao;
	private final ProjectMembershipDao membershipDao;
	private final ServicesBackend backend;
	private final InviteManager inviteManager;
	private final ProvisioningRequest request;

	@Inject
	public ProvisionTask(
			CourseDao courseDao,
			ProjectDao projectDao,
			ProjectMembershipDao membershipDao,
			InviteManager inviteManager,
			ServicesBackend backend,
			Provisioner provisioner,
			@Assisted ProvisioningRequest request) {

		this.courseDao = courseDao;
		this.projectDao = projectDao;
		this.membershipDao = membershipDao;
		this.inviteManager = inviteManager;
		this.backend = backend;
		this.provisioner = provisioner;
		this.request = request;
	}

	@Override
	public void run() {
		long projectId = prepareProvisioning();
		provisionProject(projectId);
	}

	@Transactional
	protected void provisionProject(long projectId) {
		String netId = request.getCreator().getNetId();

		User creator;
		Project project = projectDao.findById(projectId);
		log.debug("Running Provision task for project: {}", project);

		try {
			provisioner.updateProjectState(netId, new State(false, false, "Preparing to provision project..."));
			creator = membershipDao.findByProjectId(project.getId()).get(0);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			if (project != null) {
				projectDao.remove(project);
			}
			provisioner.updateProjectState(netId, new State(true, true, "Could not provision missing project!"));
			return;
		}

		log.debug("Found project and creator");
		try {
			provisioner.updateProjectState(netId, new State(false, false, "Provisioning source code repository..."));
			String repositoryUrl = createVersionControlRepository(project, creator);
			project.setSourceCodeUrl(repositoryUrl);
			projectDao.persist(project);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			removeVersionControlRepository(project, creator);
			projectDao.remove(project);
			provisioner.updateProjectState(netId, new State(true, true, "Could not provision source code repository!"));
			return;
		}
		log.debug("Created a repository for project {}", project);
		try {
			provisioner.updateProjectState(netId, new State(false, false, "Configuring build server project..."));
			createContinuousIntegrationJob(project, creator);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			removeContinuousIntegrationJob(project, creator);
			removeVersionControlRepository(project, creator);
			projectDao.remove(project);
			provisioner.updateProjectState(netId, new State(true, true, "Could not configure build server project!"));
			return;
		}
		log.debug("Created a CI Job for project {}", project);
		project.setDeployed(true);
		projectDao.persist(project);

		if (!request.getInvited().isEmpty()) {
			log.debug("Inviting project members");
			provisioner.updateProjectState(netId, new State(false, false, "Inviting project members..."));

			for (String invite : request.getInvited()) {
				try {
					inviteManager.inviteUser(creator, invite, project);
				} catch (InviteException e) {
					log.warn(e.getMessage(), e);
				}
			}
		}

		provisioner.updateProjectState(netId, new State(true, false, "Successfully provisioned project!"));
	}

	private String createVersionControlRepository(Project project, User creator) throws ServiceException {
		log.debug("Creating source code repository");
		ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getDisplayName(), creator.getEmail());
		RepositoryRepresentation repo = new RepositoryRepresentation(project.getProjectId(), serviceUser);
		for (ProjectMembership membership : project.getMembers()) {
			User member = membership.getUser();
			repo.addMember(new ServiceUser(member.getNetId(), member.getEmail(), member.getDisplayName()));
		}

		// Grant access to the Git user.
		repo.addMember(new ServiceUser("git", null, "git"));
		if (project.getCourse().hasTemplateUrl()) {
			String templateUrl = project.getCourse().getTemplateUrl();
			return request.getVersionControlService().createRepository(repo, templateUrl);
		} else {
			return request.getVersionControlService().createRepository(repo);
		}
	}

	private void removeVersionControlRepository(Project project, User creator) {
		log.debug("Removing repository for project {}", project.getId());
		try {
			request.getVersionControlService().removeRepository(project.getProjectId());
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

	private void createContinuousIntegrationJob(Project project, User creator) throws ServiceException {
		log.debug("Creating CI job for project {}", project.getId());

		ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getDisplayName(), creator.getEmail());
		backend.ensureUserExists(request.getContinuousIntegrationService(), serviceUser);

		BuildIdentifier buildId = new BuildIdentifier(project.getProjectId(), serviceUser);
		BuildProject buildRequest = new BuildProject(buildId, project.getSourceCodeUrl());
		for (ProjectMembership membership : project.getMembers()) {
			User member = membership.getUser();
			ServiceUser user = new ServiceUser(member.getNetId(), member.getEmail(), member.getDisplayName());
			backend.ensureUserExists(request.getContinuousIntegrationService(), user);
			buildRequest.addMember(user);
		}

		URL url = request.getContinuousIntegrationService().createBuildProject(buildRequest);
		project.setContinuousIntegrationUrl(url);
	}

	private void removeContinuousIntegrationJob(Project project, User creator) {
		try {
			ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getDisplayName(), creator.getEmail());
			BuildIdentifier buildId = new BuildIdentifier(project.getProjectId(), serviceUser);
			request.getContinuousIntegrationService().removeBuildProject(buildId);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

	private long prepareProvisioning() {
		if (alreadyMemberOfCourseProject(request.getCreator(), request.getCourseId())) {
			throw new ProvisioningException("You're already a member of a project for this course!");
		}

		try {
			return persistToDatabase();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new ProvisioningException("Could not create project!");
		}
	}

	private long persistToDatabase() {
		synchronized (groupAssigner) {
			return internalPersist();
		}
	}

	@Transactional
	long internalPersist() {
		Course course = courseDao.findById(request.getCourseId());
		int projectNumber = projectDao.findByCourse(course).size() + 1;
		String projectName = course.getName() + " - Group " + projectNumber;

		log.info("Preparing project: '" + projectName + "' for user: " + request.getCreator());

		Project project = new Project(projectName, course);
		project.setContinuousIntegrationService(request.getContinuousIntegrationService().getName());
		project.setVersionControlService(request.getVersionControlService().getName());

		projectDao.persist(project);
		membershipDao.persist(new ProjectMembership(request.getCreator(), project));

		return project.getId();
	}

	boolean alreadyMemberOfCourseProject(User currentUser, Long course) {
		return membershipDao.hasEnrolled(course, currentUser);
	}
}