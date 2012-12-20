package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.jaxrs.api.projects.InviteManager;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.devhub.services.ServiceException;
import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildIdentifier;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildProject;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryRepresentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.persist.Transactional;

public class ProvisionTask implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(ProvisionTask.class);

	private final long projectId;
	private final VersionControlService versioningService;
	private final ContinuousIntegrationService buildService;
	private final Provisioner provisioner;
	private final ProjectDao projectDao;
	private final ProjectMembershipDao membershipDao;
	private final InviteManager inviteManager;

	@Inject
	public ProvisionTask(ProjectDao projectDao,
			ProjectMembershipDao membershipDao,
			InviteManager inviteManager,
			@Assisted Provisioner provisioner,
			@Assisted VersionControlService versioningService,
			@Assisted ContinuousIntegrationService buildService,
			@Assisted long projectId) {

		this.projectDao = projectDao;
		this.membershipDao = membershipDao;
		this.inviteManager = inviteManager;
		this.provisioner = provisioner;
		this.versioningService = versioningService;
		this.buildService = buildService;
		this.projectId = projectId;
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

		if (project.getInvitations().size() > 0) {
			LOG.debug("Inviting project members");
			provisioner.updateProjectState(projectId, new State(false, false, "Inviting project members..."));

			try {
				for (ProjectInvitation invite : project.getInvitations()) {
					inviteManager.inviteUser(creator, invite.getEmail(), project);
				}
			} catch (Throwable e) {
				LOG.warn(e.getMessage(), e);
			}
		}

		provisioner.updateProjectState(projectId, new State(true, false, "Successfully provisioned project!"));
	}

	private String createVersionControlRepository(Project project, User creator) throws ServiceException {
		LOG.debug("Creating source code repository");
		ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getEmail());
		RepositoryIdentifier repositoryId = new RepositoryIdentifier(project.getSafeName(), serviceUser);
		RepositoryRepresentation request = new RepositoryRepresentation(repositoryId, project.getSafeName());
		for (ProjectMembership membership : project.getMembers()) {
			User member = membership.getUser();
			request.addMember(new ServiceUser(member.getNetId(), member.getEmail()));
		}

		// Grant access to the Git user.
		request.addMember(new ServiceUser("git", null));
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
			ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getEmail());
			RepositoryIdentifier repositoryId = new RepositoryIdentifier(project.getSafeName(), serviceUser);
			versioningService.removeRepository(repositoryId);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void createContinuousIntegrationJob(Project project, User creator) throws ServiceException {
		LOG.debug("Creating CI job for project {}", project.getId());
		ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getEmail());
		BuildIdentifier buildId = new BuildIdentifier(project.getSafeName(), serviceUser);
		BuildProject request = new BuildProject(buildId, project.getSourceCodeUrl());
		for (ProjectMembership membership : project.getMembers()) {
			User member = membership.getUser();
			request.addMember(new ServiceUser(member.getNetId(), member.getEmail()));
		}
		String url = buildService.createBuildProject(request);
		project.setContinuousIntegrationUrl(url);
	}

	private void removeContinuousIntegrationJob(Project project, User creator) {
		try {
			ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getEmail());
			BuildIdentifier buildId = new BuildIdentifier(project.getSafeName(), serviceUser);
			buildService.removeBuildProject(buildId);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
	}
}