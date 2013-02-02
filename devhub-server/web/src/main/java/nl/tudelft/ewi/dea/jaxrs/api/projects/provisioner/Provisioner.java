package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

import java.util.HashSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.NoResultException;

import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.jaxrs.api.projects.CourseProjectRequest;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import com.google.inject.persist.Transactional;

@Singleton
public class Provisioner {

	public static final Logger LOG = LoggerFactory.getLogger(Provisioner.class);

	private final ScheduledExecutorService executor;
	private final Cache<Long, State> stateCache;
	private final CourseDao courseDao;
	private final ProjectDao projectDao;
	private final ProjectMembershipDao membershipDao;

	private final ProvisionTaskFactory factory;

	@Inject
	public Provisioner(ProvisionTaskFactory factory, CourseDao courseDao,
			ProjectDao projectDao, ProjectMembershipDao membershipDao,
			ScheduledExecutorService executor) {

		this.factory = factory;
		this.courseDao = courseDao;
		this.projectDao = projectDao;
		this.membershipDao = membershipDao;
		this.executor = executor;

		stateCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(500).build();
	}

	public long provision(CourseProjectRequest courseProject, User owner,
			VersionControlService versioningService, ContinuousIntegrationService buildService) {

		Project project = prepareProvisioning(courseProject, owner, versioningService, buildService);

		long projectId = project.getId();
		HashSet<String> invited = Sets.newHashSet(courseProject.getInvites());

		updateProjectState(projectId, new State(false, false, "Preparing to provision project..."));
		executor.submit(factory.create(this, versioningService, buildService, projectId, invited));

		return projectId;
	}

	private Project prepareProvisioning(CourseProjectRequest courseProject, User owner,
			VersionControlService versioningService, ContinuousIntegrationService buildService) {

		if (alreadyMemberOfCourseProject(owner, courseProject.getCourse())) {
			throw new ProvisioningException("You're already a member of a project for this course!");
		}

		Project project;
		try {
			project = persistToDatabase(courseProject, owner, versioningService, buildService);
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
			throw new ProvisioningException("Could not create project!");
		}

		return project;
	}

	@Transactional
	synchronized Project persistToDatabase(CourseProjectRequest courseProject, User owner,
			VersionControlService versioningService, ContinuousIntegrationService buildService) {

		Course course = courseDao.findById(courseProject.getCourse());
		int projectNumber = projectDao.findByCourse(course).size() + 1;
		String projectName = course.getName() + " - Group " + projectNumber;

		Project project = new Project(projectName, course);
		project.setContinuousIntegrationService(buildService.getName());
		project.setVersionControlService(versioningService.getName());

		ProjectMembership membership = new ProjectMembership(owner, project);

		projectDao.persist(project);
		membershipDao.persist(membership);

		return project;
	}

	@Transactional
	boolean alreadyMemberOfCourseProject(User currentUser, Long course) {
		return membershipDao.hasEnrolled(course, currentUser);
	}

	void updateProjectState(long projectId, State state) {
		stateCache.put(projectId, state);
	}

	public State getState(long projectId) {
		State state = stateCache.getIfPresent(projectId);
		if (state == null) {
			try {
				projectDao.findById(projectId);
				return new State(true, false, "Project has been provisioned!");
			} catch (NoResultException e) {
				return new State(true, true, "Project was not provisioned!");
			}
		}
		return state;
	}
}
