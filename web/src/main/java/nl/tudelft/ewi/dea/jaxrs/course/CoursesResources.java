package nl.tudelft.ewi.dea.jaxrs.course;

import javax.inject.Inject;
import javax.inject.Singleton;
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
import nl.tudelft.ewi.dea.model.Course;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Path("courses")
@Produces(MediaType.APPLICATION_JSON)
public class CoursesResources {

	private static final Logger LOG = LoggerFactory.getLogger(CoursesResources.class);

	private final CourseDao courseDao;

	@Inject
	public CoursesResources(final CourseDao courseDao) {
		this.courseDao = courseDao;
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("checkName")
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
	public Course create(final CourseCreationRequest request) {
		// TODO Implement
		throw new NotImplementedException("Not yet implemented");
	}

	public static class CourseCreationRequest {

	}

}
