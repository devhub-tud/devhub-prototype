package nl.tudelft.ewi.dea.jaxrs.api.course;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/course")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

	private final CourseDao courseDao;
	private final ProjectDao projectDao;
	private final ProjectMembershipDao membershipDao;
	private final SecurityProvider securityProvider;

	@Inject
	public CourseResource(CourseDao courseDao, ProjectDao projectDao,
			ProjectMembershipDao membershipDao, SecurityProvider securityProvider) {

		this.courseDao = courseDao;
		this.projectDao = projectDao;
		this.membershipDao = membershipDao;
		this.securityProvider = securityProvider;
	}

	@GET
	@Path("{id}/enroll")
	@Transactional
	public Response enroll(@PathParam("id") final long id) {
		final Course course = courseDao.findById(id);
		final User currentUser = securityProvider.getUser();

		if (membershipDao.hasEnrolled(course.getId(), currentUser)) {
			return Response.status(Status.CONFLICT).entity("User: " + currentUser.getDisplayName()
					+ " is already enrolled in the course: " + course.getName()).build();
		}

		// TODO: Make this into the form: IN4321: Functional programming - Group 2
		final String projectName = course.getName() + " - " + currentUser.getDisplayName();
		final Project project = new Project(projectName, course);
		projectDao.persist(project);

		final ProjectMembership membership = currentUser.addProjectMembership(project);
		membershipDao.persist(membership);

		return Response.ok().build();
	}

}
