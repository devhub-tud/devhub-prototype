package nl.tudelft.ewi.dea.jaxrs.api.course;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.User;

import org.apache.shiro.SecurityUtils;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/courses")
@Produces(MediaType.APPLICATION_JSON)
public class CoursesResources {

	private final CourseDao courseDao;
	private final UserDao userDao;

	@Inject
	public CoursesResources(CourseDao courseDao, UserDao userDao) {
		this.courseDao = courseDao;
		this.userDao = userDao;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response find(@QueryParam("enrolled") Boolean enrolled, @QueryParam("substring") String subString) {
		List<CourseDto> courses = CourseDto.convert(courseDao.find(enrolled, subString));
		GenericEntity<List<CourseDto>> entity = new GenericEntity<List<CourseDto>>(courses) {};
		return Response.ok(entity).build();
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("checkName")
	public Response checkCourseName(@QueryParam("name") String name) {
		try {
			courseDao.findByName(name);
			return Response.status(Status.CONFLICT).entity("already-taken").build();
		} catch (NoResultException e) {
			return Response.ok("ok").build();
		}
	}

	@POST
	@Path("create")
	public Course create(CourseCreationRequest request) {
		String email = (String) SecurityUtils.getSubject().getPrincipal();
		User owner = userDao.findByEmail(email);
		Course course = new Course(request.getName(), owner);

		courseDao.persist(course);
		return course;
	}

}
