package nl.tudelft.ewi.dea.jaxrs.projects.provisioner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.naming.ServiceUnavailableException;
import javax.persistence.NoResultException;

import lombok.Data;
import nl.minicom.gitolite.manager.ConfigManager;
import nl.minicom.gitolite.manager.models.Config;
import nl.minicom.gitolite.manager.models.Permission;
import nl.minicom.gitolite.manager.models.Repository;
import nl.tudelft.ewi.dea.DevHubException;
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
import nl.tudelft.jenkins.auth.UserImpl;
import nl.tudelft.jenkins.client.JenkinsClient;
import nl.tudelft.jenkins.jobs.Job;

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
	private final JenkinsClient jenkinsClient;
	private final ConfigManager gitManager;

	private final DevHubMail mailer;
	private final String publicUrl;

	private final String gitHost;

	@Inject
	public Provisioner(Provider<UnitOfWork> units, Provider<CourseDao> courseDao, Provider<ProjectDao> projectDao,
			Provider<ProjectMembershipDao> membershipDao, Provider<ProjectInvitationDao> invitationDao, Provider<UserDao> userDao,
			DevHubMail mailer, JenkinsClient jenkinsClient, ConfigManager gitManager,
			ServerConfig config) {

		this.units = units;
		this.courseDao = courseDao;
		this.projectDao = projectDao;
		this.membershipDao = membershipDao;
		this.invitationDao = invitationDao;
		this.userDao = userDao;
		this.mailer = mailer;
		this.jenkinsClient = jenkinsClient;
		this.gitManager = gitManager;
		this.publicUrl = config.getWebUrl();
		this.gitHost = config.getGitHost();

		this.stateCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(500).build();
		this.executor = new ScheduledThreadPoolExecutor(0);
		executor.setMaximumPoolSize(1);
	}

	@Transactional
	public long provision(CourseProjectRequest courseProject, User owner) {
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

		executor.submit(new ProvisionTask(project.getId()));
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

		private ProvisionTask(long projectId) {
			this.projectId = projectId;
		}

		@Override
		public void run() {
			UnitOfWork unit = units.get();
			unit.begin();

			Project project = null;
			ProjectMembership creator = null;

			try {
				stateCache.put(projectId, new State(false, false, "Preparing to provision project..."));
				project = projectDao.get().findById(projectId);
				creator = project.getMembers().iterator().next();
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
				provisionGitRepository(project.getSafeName());
			} catch (Throwable e) {
				LOG.error(e.getMessage(), e);
				rollBackGitRepositoryCreation(project.getSafeName());
				removeProjectFromDb(project);
				stateCache.put(projectId, new State(true, true, "Could not provision source code repository!"));
				return;
			}

			try {
				stateCache.put(projectId, new State(false, false, "Configuring build server project..."));
				String creatorEmail = creator.getUser().getEmail();
				provisionJenkins(project.getSafeName(), "git@" + gitHost + ":" + project.getSafeName(), creatorEmail);
			} catch (Throwable e) {
				LOG.error(e.getMessage(), e);
				rollBackJenkinsJobCreation(project.getName());
				rollBackGitRepositoryCreation(project.getSafeName());
				removeProjectFromDb(project);
				stateCache.put(projectId, new State(true, true, "Could not configure build server project!"));
				return;
			}

			for (ProjectInvitation invitation : project.getInvitations()) {
				try {
					String creatorName = creator.getUser().getDisplayName();
					String inviteeEmail = invitation.getUser().getEmail();
					mailer.sendProjectInvite(inviteeEmail, creatorName, project.getName(), publicUrl);
				} catch (Throwable e) {
					LOG.error(e.getMessage(), e);
				}
			}

			stateCache.put(projectId, new State(true, false, "Successfully provisioned project!"));
			unit.end();
		}
	}

	private void rollBackJenkinsJobCreation(String name) {
		try {
			// TODO: Is this how you should roll a jenkins job creation back?
			Job job = jenkinsClient.retrieveJob(name);
			if (job != null) {
				jenkinsClient.deleteJob(job);
			}
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void rollBackGitRepositoryCreation(String projectName) {
		try {
			Config config = gitManager.getConfig();
			if (config.hasRepository(projectName)) {
				config.removeRepository(config.getRepository(projectName));
				gitManager.applyConfig();
			}
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
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

	private void provisionJenkins(String projectName, String gitUrl, String email) {
		List<nl.tudelft.jenkins.auth.User> owners = new ArrayList<>();
		owners.add(new UserImpl(email, email));
		jenkinsClient.createJob(projectName, gitUrl, owners);
	}

	private void provisionGitRepository(String name) throws IOException {
		try {
			Config config = gitManager.getConfig();
			if (config.hasRepository(name)) {
				throw new ProvisioningException("Repository alreay exists!");
			}

			final nl.minicom.gitolite.manager.models.User admin = config.ensureUserExists("git");
			final Repository repo = config.createRepository(name);
			repo.setPermission(admin, Permission.ALL);
			gitManager.applyConfig();
		} catch (ServiceUnavailableException e) {
			throw new DevHubException("Could not provision " + name + " because " + e.getMessage(), e);
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
