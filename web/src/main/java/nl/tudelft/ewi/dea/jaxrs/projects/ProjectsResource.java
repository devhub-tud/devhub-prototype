package nl.tudelft.ewi.dea.jaxrs.projects;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.minicom.gitolite.manager.ConfigManager;
import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;
import nl.tudelft.jenkins.client.JenkinsClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("projects")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectsResource {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectsResource.class);

	private final ConfigManager gitManager;
	private final JenkinsClient jenkinsClient;
	private final ProjectMembershipDao membershipDao;
	private final ProjectDao projectDao;
	private final UserDao userDao;
	private final CourseDao courseDao;
	private final SecurityProvider securityProvider;

	@Inject
	public ProjectsResource(UserDao userDao, CourseDao courseDao, ProjectMembershipDao membershipDao,
			ProjectDao projectDao, SecurityProvider securityProvider, ConfigManager gitManager, JenkinsClient jenkinsClient) {
		this.userDao = userDao;
		this.courseDao = courseDao;
		this.membershipDao = membershipDao;
		this.projectDao = projectDao;
		this.securityProvider = securityProvider;
		this.gitManager = gitManager;
		this.jenkinsClient = jenkinsClient;
	}

	@POST
	@Transactional
	public Response createNewProject(CourseProjectRequest request) {
		final User currentUser = securityProvider.getUser();

		if (alreadyMemberOfCourseProject(currentUser, request.getCourse())) {
			return Response.status(Status.CONFLICT).entity("You're already a member of a project for this course!").build();
		}

		Project project = createNewCourseProject(currentUser, request);
		for (String invite : request.getInvites()) {
			inviteUserToProject(project, invite);
		}

		return Response.ok().build();
	}

	private Response inviteUserToProject(Project project, String invite) {
		User user = null;
		try {
			user = userDao.findByEmail(invite);
			membershipDao.persist(new ProjectMembership(user, project));
			// TODO: Send e-mail to notify invited user of invitation.

			return null;

		} catch (NoResultException e) {
			// TODO: Make user account and invite user...
			throw e;
		}
	}

	/*
	 * This method is synchronized to ensure correct numbering of project names.
	 */
	private synchronized Project createNewCourseProject(User currentUser, CourseProjectRequest request) {
		Course course = courseDao.findById(request.getCourse());
		int projectNumber = projectDao.findByCourse(course).size() + 1;
		String projectName = course.getName() + " - Group " + projectNumber;

		Project project = new Project(projectName, course);
		ProjectMembership membership = new ProjectMembership(currentUser, project);

		projectDao.persist(project);
		membershipDao.persist(membership);

		return project;
	}

	private boolean alreadyMemberOfCourseProject(User currentUser, Long course) {
		return membershipDao.hasEnrolled(course, currentUser);
	}

	// @GET
	// @Path("checkName")
	// @Transactional
	// public Response checkProjectName(@QueryParam("name") final String name) {
	// if (!isValidProjectName(name)) {
	// return Response.status(Status.CONFLICT).entity("invalid-name").build();
	// }
	// if (!projectNameIsAvailable(name)) {
	// return Response.status(Status.CONFLICT).entity("already-taken").build();
	// }
	// return Response.ok().build();
	// }
	//
	// @POST
	// @Path("create")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Transactional
	// public Response provisionNewProject(final CreateProjectRequest request) {
	// final String name = request.getName();
	// if (!isValidProjectName(name) || !projectNameIsAvailable(name)) {
	// return
	// Response.status(Status.CONFLICT).entity("Project name is not valid!").build();
	// }
	//
	// final Response gitProvisioning = provisionGitRepository(name);
	// if (gitProvisioning.getStatus() != Status.OK.getStatusCode()) {
	// return gitProvisioning;
	// }
	//
	// return provisionJenkins(name, "git@dea.hartveld.com:" + name,
	// "M.deJong-2@student.tudelft.nl");
	// }
	//
	// private Response provisionJenkins(final String projectName, final String
	// gitUrl, final String email) {
	// final List<nl.tudelft.jenkins.auth.User> owners = new ArrayList<>();
	// owners.add(new UserImpl(email, email));
	//
	// try {
	// jenkinsClient.createJob(projectName, gitUrl, owners);
	// } catch (final Exception e) {
	// LOG.warn("Failed to create job", e);
	// return
	// Response.serverError().entity("Failed to create Jenkins job").build();
	// }
	//
	// return Response.ok().build();
	// }
	//
	// private Response provisionGitRepository(final String name) {
	// Config config = null;
	// try {
	// config = gitManager.getConfig();
	// } catch (/* TODO: Fix this in gitolite-admin */Exception e) {
	// return
	// Response.serverError().entity("Currently unable to create git repositories!").build();
	// }
	//
	// if (config.hasRepository(name)) {
	// return
	// Response.status(Status.CONFLICT).entity("Repository alreay exists!").build();
	// }
	//
	// final User admin = config.ensureUserExists("git");
	// final Repository repo = config.createRepository(name);
	// repo.setPermission(admin, Permission.ALL);
	//
	// try {
	// gitManager.applyConfig();
	// } catch (final IOException e) {
	// return
	// Response.serverError().entity("Could not create git repository!").build();
	// }
	//
	// return Response.ok().build();
	// }
	//
	// private boolean isValidProjectName(final String name) {
	// return name != null && name.matches("[a-zA-Z0-9]{4,}");
	// }
	//
	// private boolean projectNameIsAvailable(final String name) {
	// try {
	// return !gitManager.getConfig().hasRepository(name);
	// } catch (final IOException e) {
	// LOG.error(e.getMessage(), e);
	// }
	// return false;
	// }

}
