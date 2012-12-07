package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildIdentifier;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildProject;
import nl.tudelft.ewi.devhub.services.models.ServiceResponse;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.CreatedRepositoryResponse;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryRepresentation;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.persist.Transactional;

public class ProvisionTask implements Runnable {

	private final long projectId;
	private final VersionControlService versioningService;
	private final ContinuousIntegrationService buildService;
	private final Provisioner provisioner;
	private final ProjectDao projectDao;
	private final ProjectMembershipDao membershipDao;

	@Inject
	public ProvisionTask(ProjectDao projectDao,
			ProjectMembershipDao membershipDao,
			@Assisted Provisioner provisioner,
			@Assisted VersionControlService versioningService,
			@Assisted ContinuousIntegrationService buildService,
			@Assisted long projectId) {

		this.projectDao = projectDao;
		this.membershipDao = membershipDao;
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

		try {
			provisioner.updateProjectState(projectId, new State(false, false, "Preparing to provision project..."));
			project = projectDao.findById(projectId);
			creator = membershipDao.findByProjectId(projectId).get(0);
		} catch (Throwable e) {
			Provisioner.LOG.error(e.getMessage(), e);
			if (project != null) {
				projectDao.remove(project);
			}
			provisioner.updateProjectState(projectId, new State(true, true, "Could not provision missing project!"));
			return;
		}

		try {
			provisioner.updateProjectState(projectId, new State(false, false, "Provisioning source code repository..."));
			ServiceResponse response = createVersionControlRepository(project, creator);

			if (!response.isSuccess()) {
				throw new ProvisioningException(response.getMessage());
			}

		} catch (Throwable e) {
			Provisioner.LOG.error(e.getMessage(), e);
			removeVersionControlRepository(project, creator);
			projectDao.remove(project);
			provisioner.updateProjectState(projectId, new State(true, true, "Could not provision source code repository!"));
			return;
		}

		try {
			provisioner.updateProjectState(projectId, new State(false, false, "Configuring build server project..."));
			ServiceResponse response = createContinuousIntegrationJob(project, creator);

			if (!response.isSuccess()) {
				throw new ProvisioningException(response.getMessage());
			}
		} catch (Throwable e) {
			Provisioner.LOG.error(e.getMessage(), e);
			removeContinuousIntegrationJob(project, creator);
			removeVersionControlRepository(project, creator);
			projectDao.remove(project);
			provisioner.updateProjectState(projectId, new State(true, true, "Could not configure build server project!"));
			return;
		}

		provisioner.updateProjectState(projectId, new State(true, false, "Successfully provisioned project!"));
	}

	private ServiceResponse createVersionControlRepository(Project project, User creator) {
		ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getEmail());
		RepositoryIdentifier repositoryId = new RepositoryIdentifier(project.getSafeName(), serviceUser);
		RepositoryRepresentation request = new RepositoryRepresentation(repositoryId, project.getSafeName());
		for (ProjectMembership membership : project.getMembers()) {
			User member = membership.getUser();
			request.addMember(new ServiceUser(member.getNetId(), member.getEmail()));
		}

		// Grant access to the Git user.
		request.addMember(new ServiceUser("git", null));

		try {
			Future<CreatedRepositoryResponse> createRepository = versioningService.createRepository(request);
			CreatedRepositoryResponse serviceResponse = createRepository.get();
			if (serviceResponse.isSuccess()) {
				project.setSourceCodeUrl(serviceResponse.getRepositoryUrl());
				projectDao.merge(project);
			}

			return serviceResponse;
		} catch (ExecutionException | InterruptedException e) {
			throw new ProvisioningException("Failed to provision new source code repository", e);
		}
	}

	private ServiceResponse removeVersionControlRepository(Project project, User creator) {
		ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getEmail());
		RepositoryIdentifier repositoryId = new RepositoryIdentifier(project.getSafeName(), serviceUser);

		try {
			Future<ServiceResponse> removeRepository = versioningService.removeRepository(repositoryId);
			return removeRepository.get();
		} catch (ExecutionException | InterruptedException e) {
			return new ServiceResponse(false, "Uknown error occurred when removing source code repository!");
		}
	}

	private ServiceResponse createContinuousIntegrationJob(Project project, User creator) {
		ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getEmail());
		BuildIdentifier buildId = new BuildIdentifier(project.getSafeName(), serviceUser);
		BuildProject request = new BuildProject(buildId, project.getSourceCodeUrl());
		for (ProjectMembership membership : project.getMembers()) {
			User member = membership.getUser();
			request.addMember(new ServiceUser(member.getNetId(), member.getEmail()));
		}

		try {
			Future<ServiceResponse> createBuildProject = buildService.createBuildProject(request);
			return createBuildProject.get();
		} catch (ExecutionException | InterruptedException e) {
			return new ServiceResponse(false, "Uknown error occurred when creating continuous integration job!");
		}
	}

	private ServiceResponse removeContinuousIntegrationJob(Project project, User creator) {
		ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getEmail());
		BuildIdentifier buildId = new BuildIdentifier(project.getSafeName(), serviceUser);

		try {
			Future<ServiceResponse> removeBuildProject = buildService.removeBuildProject(buildId);
			return removeBuildProject.get();
		} catch (ExecutionException | InterruptedException e) {
			return new ServiceResponse(false, "Uknown error occurred when removing continuous integration job!");
		}
	}
}