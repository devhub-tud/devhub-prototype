package nl.tudelft.ewi.dea.jaxrs.course;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.NoResultException;
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
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("course")
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

	private static final Logger LOG = LoggerFactory.getLogger(CourseResource.class);

	private final Provider<Renderer> renderers;

	private final CourseDao courseDao;
	private final ProjectDao projectDao;
	private final ProjectMembershipDao membershipDao;

	private final SecurityProvider securityProvider;

	@Inject
	public CourseResource(final Provider<Renderer> renderers, final CourseDao courseDao, final ProjectDao projectDao, final ProjectMembershipDao membershipDao, final SecurityProvider securityProvider) {
		this.renderers = renderers;

		this.courseDao = courseDao;
		this.projectDao = projectDao;
		this.membershipDao = membershipDao;

		this.securityProvider = securityProvider;
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public String findCourse(@PathParam("id") final long id) {

		LOG.trace("Finding course: {}", id);

		// TODO: Use the exception mapper to map NoResultFound to 404.
		final Course course = courseDao.findById(id);

		final List<Project> projects = projectDao.findByCourse(course);

		return renderers.get()
				.setValue("course", course)
				.setValue("projects", projects)
				.setValue("scripts", Lists.newArrayList("course.js"))
				.render("course.tpl");

	}

	@GET
	@Path("{id}/enroll")
	@Transactional
	public Response enroll(@PathParam("id") final long id) {

		LOG.trace("Enroll in course: {}", id);

		final Course course;
		try {
			course = courseDao.findById(id);
		} catch (final NoResultException e) {
			LOG.trace("Course does not exist: {}", id, e);
			return Response.status(Status.NOT_FOUND).entity("Course does not exist").build();
		}

		final User currentUser = securityProvider.getUser();

		final String projectName = course.getName() + "-" + currentUser.getDisplayName();

		final Project project = new Project(projectName, course);
		projectDao.persist(project);

		final ProjectMembership membership = currentUser.addProjectMembership(project);
		membershipDao.persist(membership);

		return Response.ok().build();

	}

}
