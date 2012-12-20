package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectInvitationDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.jaxrs.api.projects.CourseProjectRequest;
import nl.tudelft.ewi.dea.jaxrs.api.projects.InviteManager;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;
import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class Provisioner {

	public static final Logger LOG = LoggerFactory.getLogger(Provisioner.class);

	private final ScheduledExecutorService executor;
	private final Cache<Long, State> stateCache;
	private final Provider<CourseDao> courseDao;
	private final Provider<ProjectDao> projectDao;
	private final Provider<ProjectMembershipDao> membershipDao;
	private final Provider<ProjectInvitationDao> inviteDao;

	private final ProvisionTaskFactory factory;
	private final InviteManager inviteManager;
	private final SecurityProvider securityProvider;

	@Inject
	public Provisioner(ProvisionTaskFactory factory, Provider<CourseDao> courseDao,
			Provider<ProjectDao> projectDao, Provider<ProjectMembershipDao> membershipDao,
			Provider<ProjectInvitationDao> inviteDao, InviteManager inviteManager,
			ScheduledExecutorService executor, SecurityProvider securityProvider) {

		this.factory = factory;
		this.courseDao = courseDao;
		this.projectDao = projectDao;
		this.membershipDao = membershipDao;
		this.inviteDao = inviteDao;
		this.inviteManager = inviteManager;
		this.securityProvider = securityProvider;
		this.executor = executor;

		stateCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(500).build();
	}

	public long provision(CourseProjectRequest courseProject, User owner,
			VersionControlService versioningService, ContinuousIntegrationService buildService) {

		Project project = prepareProvisioning(courseProject, owner);

		long projectId = project.getId();
		updateProjectState(projectId, new State(false, false, "Preparing to provision project..."));
		executor.submit(factory.create(this, versioningService, buildService, projectId));

		return projectId;
	}

	Project prepareProvisioning(CourseProjectRequest courseProject, User owner) {
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

		return project;
	}

	@Transactional
	synchronized Project persistToDatabase(CourseProjectRequest courseProject, User owner) {
		Course course = courseDao.get().findById(courseProject.getCourse());
		int projectNumber = projectDao.get().findByCourse(course).size() + 1;
		String projectName = course.getName() + "/Group " + projectNumber;

		Project project = new Project(projectName, course);
		ProjectMembership membership = new ProjectMembership(owner, project);

		projectDao.get().persist(project);
		membershipDao.get().persist(membership);

		User inviter = securityProvider.getUser();
		for (String invite : courseProject.getInvites()) {
			inviteManager.inviteUser(inviter, invite, project);
		}

		return project;
	}

	@Transactional
	boolean alreadyInvitedOrMember(User user, Project project) {
		if (membershipDao.get().hasEnrolled(project.getCourse().getId(), user)) {
			return true;
		}

		try {
			inviteDao.get().findByProjectAndEMail(project, user.getEmail());
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}

	@Transactional
	boolean alreadyMemberOfCourseProject(User currentUser, Long course) {
		return membershipDao.get().hasEnrolled(course, currentUser);
	}

	@Transactional
	void removeProjectFromDb(Project project) {
		try {
			projectDao.get().remove(project);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
	}

	void updateProjectState(long projectId, State state) {
		stateCache.put(projectId, state);
	}

	@Transactional
	void update(Project project) {
		try {
			projectDao.get().merge(project);
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
