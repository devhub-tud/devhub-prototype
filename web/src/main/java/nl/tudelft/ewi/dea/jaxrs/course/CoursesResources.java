package nl.tudelft.ewi.dea.jaxrs.course;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.User;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("courses")
@Produces(MediaType.APPLICATION_JSON)
public class CoursesResources {

	private static final Logger LOG = LoggerFactory.getLogger(CoursesResources.class);

	private final CourseDao courseDao;
	private final UserDao userDao;

	@Inject
	public CoursesResources(final CourseDao courseDao, final UserDao userDao) {
		this.courseDao = courseDao;
		this.userDao = userDao;
	}

	@GET
	public List<Course> findBySubString(@QueryParam("substring") final String subString) {

		LOG.trace("Find by substring: {}", subString);

		final List<Course> courses = courseDao.findBySubString(subString);

		return courses;

	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("checkName")
	@Transactional
	public Response checkCourseName(@QueryParam("name") final String name) {

		LOG.trace("Checking course name: {}", name);

		boolean courseExists = true;
		try {
			courseDao.findByName(name);
		} catch (final NoResultException e) {
			courseExists = false;
		}

		if (courseExists) {
			return Response.status(Status.CONFLICT).entity("already-taken").build();
		} else {
			return Response.ok("ok").build();
		}

	}

	@POST
	@Path("create")
	@Transactional
	public Course create(final CourseCreationRequest request) {

		LOG.trace("Create: {}", request);

		final String email = (String) SecurityUtils.getSubject().getPrincipal();
		final User owner = userDao.findByEmail(email);

		final String name = request.getName();

		final Course course = new Course(name, owner);
		courseDao.persist(course);

		return course;

	}

}
