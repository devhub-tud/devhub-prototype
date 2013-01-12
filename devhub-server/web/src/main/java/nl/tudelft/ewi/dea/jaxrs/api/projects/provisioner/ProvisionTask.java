package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

import java.util.Set;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.jaxrs.api.projects.InviteException;
import nl.tudelft.ewi.dea.jaxrs.api.projects.InviteManager;
import nl.tudelft.ewi.dea.jaxrs.api.projects.services.ServicesBackend;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.devhub.services.ServiceException;
import nl.tudelft.ewi.devhub.services.ServiceProvider;
import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildIdentifier;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildProject;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryRepresentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.persist.Transactional;

public class ProvisionTask implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(ProvisionTask.class);

	private final long projectId;
	private final Provisioner provisioner;
	private final ProjectDao projectDao;
	private final ServicesBackend backend;
	private final ProjectMembershipDao membershipDao;
	private final InviteManager inviteManager;
	private final ServiceProvider services;

	private final Set<String> invited;

	private VersionControlService versioningService;
	private ContinuousIntegrationService buildService;

	@Inject
	public ProvisionTask(ProjectDao projectDao,
			ProjectMembershipDao membershipDao,
			InviteManager inviteManager,
			ServiceProvider services,
			ServicesBackend backend,
			@Assisted Provisioner provisioner,
			@Assisted long projectId,
			@Assisted Set<String> invited) {

		this.projectDao = projectDao;
		this.membershipDao = membershipDao;
		this.inviteManager = inviteManager;
		this.backend = backend;
		this.provisioner = provisioner;
		this.services = services;
		this.projectId = projectId;
		this.invited = invited;
	}

	@Override
	@Transactional
	public void run() {
		Project project = null;
		User creator = null;

		LOG.debug("Running Provision task for project id {}", projectId);

		try {
			provisioner.updateProjectState(projectId, new State(false, false, "Preparing to provision project..."));
			project = projectDao.findById(projectId);
			creator = membershipDao.findByProjectId(projectId).get(0);
			versioningService = services.getVersionControlService(project.getVersionControlService());
			buildService = services.getContinuousIntegrationService(project.getContinuousIntegrationService());
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			if (project != null) {
				projectDao.remove(project);
			}
			provisioner.updateProjectState(projectId, new State(true, true, "Could not provision missing project!"));
			return;
		}

		LOG.debug("Found project and creator");
		try {
			provisioner.updateProjectState(projectId, new State(false, false, "Provisioning source code repository..."));
			String repositoryUrl = createVersionControlRepository(project, creator);
			project.setSourceCodeUrl(repositoryUrl);
			projectDao.persist(project);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			removeVersionControlRepository(project, creator);
			projectDao.remove(project);
			provisioner.updateProjectState(projectId, new State(true, true, "Could not provision source code repository!"));
			return;
		}
		LOG.debug("Created a repository for project {}", project);
		try {
			provisioner.updateProjectState(projectId, new State(false, false, "Configuring build server project..."));
			createContinuousIntegrationJob(project, creator);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			removeContinuousIntegrationJob(project, creator);
			removeVersionControlRepository(project, creator);
			projectDao.remove(project);
			provisioner.updateProjectState(projectId, new State(true, true, "Could not configure build server project!"));
			return;
		}
		LOG.debug("Created a CI Job for project {}", project);
		project.setDeployed(true);
		projectDao.persist(project);

		if (!invited.isEmpty()) {
			LOG.debug("Inviting project members");
			provisioner.updateProjectState(projectId, new State(false, false, "Inviting project members..."));

			for (String invite : invited) {
				try {
					inviteManager.inviteUser(creator, invite, project);
				} catch (InviteException e) {
					LOG.warn(e.getMessage(), e);
				}
			}
		}

		provisioner.updateProjectState(projectId, new State(true, false, "Successfully provisioned project!"));
	}

	private String createVersionControlRepository(Project project, User creator) throws ServiceException {
		LOG.debug("Creating source code repository");
		ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getDisplayName(), creator.getEmail());
		RepositoryRepresentation request = new RepositoryRepresentation(project.getProjectId(), serviceUser);
		for (ProjectMembership membership : project.getMembers()) {
			User member = membership.getUser();
			request.addMember(new ServiceUser(member.getNetId(), member.getEmail(), member.getDisplayName()));
		}

		// Grant access to the Git user.
		request.addMember(new ServiceUser("git", null, "git"));
		if (project.getCourse().hasTemplateUrl()) {
			String templateUrl = project.getCourse().getTemplateUrl();
			return versioningService.createRepository(request, templateUrl);
		} else {
			return versioningService.createRepository(request);
		}
	}

	private void removeVersionControlRepository(Project project, User creator) {
		LOG.debug("Removing repository for project {}", project.getId());
		try {
			versioningService.removeRepository(project.getProjectId());
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void createContinuousIntegrationJob(Project project, User creator) throws ServiceException {
		LOG.debug("Creating CI job for project {}", project.getId());

		ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getDisplayName(), creator.getEmail());
		backend.ensureUserExists(buildService, serviceUser);

		BuildIdentifier buildId = new BuildIdentifier(project.getProjectId(), serviceUser);
		BuildProject request = new BuildProject(buildId, project.getSourceCodeUrl());
		for (ProjectMembership membership : project.getMembers()) {
			User member = membership.getUser();
			ServiceUser user = new ServiceUser(member.getNetId(), member.getEmail(), member.getDisplayName());
			backend.ensureUserExists(buildService, user);
			request.addMember(user);
		}

		String url = buildService.createBuildProject(request);
		project.setContinuousIntegrationUrl(url);
	}

	private void removeContinuousIntegrationJob(Project project, User creator) {
		try {
			ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getDisplayName(), creator.getEmail());
			BuildIdentifier buildId = new BuildIdentifier(project.getProjectId(), serviceUser);
			buildService.removeBuildProject(buildId);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
	}
}