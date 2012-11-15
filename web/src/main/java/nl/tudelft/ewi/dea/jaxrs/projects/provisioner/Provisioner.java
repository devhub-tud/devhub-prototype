package nl.tudelft.ewi.dea.jaxrs.projects.provisioner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import lombok.Data;
import nl.tudelft.ewi.dea.ServerConfig;
import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectInvitationDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.jaxrs.projects.CourseProjectRequest;
import nl.tudelft.ewi.dea.mail.DevHubMail;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildIdentifier;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildProject;
import nl.tudelft.ewi.devhub.services.models.ServiceResponse;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.RepositoryRepresentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

public class Provisioner {

	public static final Logger LOG = LoggerFactory.getLogger(Provisioner.class);

	private final ScheduledThreadPoolExecutor executor;
	private final Cache<Long, State> stateCache;
	private final Provider<CourseDao> courseDao;
	private final Provider<ProjectDao> projectDao;
	private final Provider<ProjectMembershipDao> membershipDao;
	private final Provider<ProjectInvitationDao> invitationDao;
	private final Provider<UserDao> userDao;

	private final Provider<UnitOfWork> units;

	private final DevHubMail mailer;
	private final String publicUrl;

	@Inject
	public Provisioner(Provider<UnitOfWork> units, Provider<CourseDao> courseDao, Provider<ProjectDao> projectDao,
			Provider<ProjectMembershipDao> membershipDao, Provider<ProjectInvitationDao> invitationDao, Provider<UserDao> userDao,
			DevHubMail mailer, ServerConfig config) {

		this.units = units;
		this.courseDao = courseDao;
		this.projectDao = projectDao;
		this.membershipDao = membershipDao;
		this.invitationDao = invitationDao;
		this.userDao = userDao;
		this.mailer = mailer;
		this.publicUrl = config.getWebUrl();

		this.stateCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(500).build();
		this.executor = new ScheduledThreadPoolExecutor(0);
		executor.setMaximumPoolSize(1);
	}

	@Transactional
	public long provision(CourseProjectRequest courseProject, User owner,
			VersionControlService versioningService, ContinuousIntegrationService buildService) {

		if (alreadyMemberOfCourseProject(owner, courseProject.getCourse())) {
			throw new ProvisioningException("You're already a member of a project for this course!");
		}

		Project project;
		try {
			project = persistToDatabase(courseProject, owner);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new ProvisioningException("Could not create project!");
		}

		try {
			for (String invite : courseProject.getInvites()) {
				inviteToProject(project, invite);
			}
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			removeProjectFromDb(project);
			throw new ProvisioningException("Could not invite users!");
		}

		executor.submit(new ProvisionTask(versioningService, buildService, project.getId()));
		return project.getId();
	}

	@Transactional
	synchronized Project persistToDatabase(CourseProjectRequest courseProject, User owner) {
		Course course = courseDao.get().findById(courseProject.getCourse());
		int projectNumber = projectDao.get().findByCourse(course).size() + 1;
		String projectName = course.getName() + " - Group " + projectNumber;

		Project project = new Project(projectName, course);
		ProjectMembership membership = new ProjectMembership(owner, project);

		projectDao.get().persist(project);
		membershipDao.get().persist(membership);

		return project;
	}

	private void inviteToProject(Project project, String email) {
		User user = null;
		try {
			user = userDao.get().findByEmail(email);
			if (!alreadyInvitedOrMember(user, project)) {
				invitationDao.get().persist(new ProjectInvitation(user, project));
			}
		} catch (NoResultException e) {
			// TODO: Make user account and invite user...
			throw e;
		}
	}

	@Transactional
	boolean alreadyInvitedOrMember(User user, Project project) {
		if (membershipDao.get().hasEnrolled(project.getCourse().getId(), user)) {
			return true;
		}

		try {
			invitationDao.get().findByProjectAndUser(project, user);
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}

	@Transactional
	boolean alreadyMemberOfCourseProject(User currentUser, Long course) {
		return membershipDao.get().hasEnrolled(course, currentUser);
	}

	@Data
	public class State {
		private final boolean finished;
		private final boolean failures;
		private final String message;
	}

	private class ProvisionTask implements Runnable {

		private final long projectId;
		private final VersionControlService versioningService;
		private final ContinuousIntegrationService buildService;

		private ProvisionTask(VersionControlService versioningService, ContinuousIntegrationService buildService, long projectId) {
			this.versioningService = versioningService;
			this.buildService = buildService;
			this.projectId = projectId;
		}

		@Override
		public void run() {
			UnitOfWork unit = units.get();
			unit.begin();

			Project project = null;
			User creator = null;

			try {
				stateCache.put(projectId, new State(false, false, "Preparing to provision project..."));
				project = projectDao.get().findById(projectId);
				creator = project.getMembers().iterator().next().getUser();
			} catch (Throwable e) {
				LOG.error(e.getMessage(), e);
				if (project != null) {
					removeProjectFromDb(project);
				}
				stateCache.put(projectId, new State(true, true, "Could not provision missing project!"));
				return;
			}

			try {
				stateCache.put(projectId, new State(false, false, "Provisioning source code repository..."));
				ServiceResponse response = createVersionControlRepository(project, creator);
				stateCache.put(projectId, new State(false, !response.isSuccess(), response.getMessage()));

			} catch (Throwable e) {
				LOG.error(e.getMessage(), e);
				removeVersionControlRepository(project, creator);
				removeProjectFromDb(project);
				stateCache.put(projectId, new State(true, true, "Could not provision source code repository!"));
				return;
			}

			try {
				stateCache.put(projectId, new State(false, false, "Configuring build server project..."));

				createContinuousIntegrationJob(project, creator);
			} catch (Throwable e) {
				LOG.error(e.getMessage(), e);
				removeContinuousIntegrationJob(project, creator);
				removeVersionControlRepository(project, creator);
				removeProjectFromDb(project);
				stateCache.put(projectId, new State(true, true, "Could not configure build server project!"));
				return;
			}

			for (ProjectInvitation invitation : project.getInvitations()) {
				try {
					String creatorName = creator.getDisplayName();
					String inviteeEmail = invitation.getUser().getEmail();
					mailer.sendProjectInvite(inviteeEmail, creatorName, project.getName(), publicUrl);
				} catch (Throwable e) {
					LOG.error(e.getMessage(), e);
				}
			}

			stateCache.put(projectId, new State(true, false, "Successfully provisioned project!"));
			unit.end();
		}

		private ServiceResponse createVersionControlRepository(Project project, User creator) {
			ServiceUser serviceUser = new ServiceUser(creator.getNetId(), creator.getEmail());
			RepositoryIdentifier repositoryId = new RepositoryIdentifier(project.getSafeName(), serviceUser);
			RepositoryRepresentation request = new RepositoryRepresentation(repositoryId, project.getSafeName());
			for (ProjectMembership membership : project.getMembers()) {
				User member = membership.getUser();
				request.addMember(new ServiceUser(member.getNetId(), member.getEmail()));
			}

			try {
				Future<ServiceResponse> createRepository = versioningService.createRepository(request);
				return createRepository.get();
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

	@Transactional
	void removeProjectFromDb(Project project) {
		try {
			projectDao.get().remove(project);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public State getState(long projectId) {
		State state = stateCache.getIfPresent(projectId);
		if (state == null) {
			try {
				projectDao.get().findById(projectId);
				return new State(true, false, "Project has been provisioned!");
			} catch (NoResultException e) {
				return new State(true, true, "Project was not provisioned!");
			}
		}
		return state;
	}
}
