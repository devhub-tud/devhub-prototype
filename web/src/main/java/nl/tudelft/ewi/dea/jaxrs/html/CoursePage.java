package nl.tudelft.ewi.dea.jaxrs.html;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("course")
@Produces(MediaType.APPLICATION_JSON)
public class CoursePage {

	private final Renderer renderer;
	private final CourseDao courseDao;
	private final ProjectDao projectDao;
	private final SecurityProvider securityProvider;
	private final UserDao userDao;

	@Inject
	public CoursePage(Renderer renderer, UserDao userDao, CourseDao courseDao,
			ProjectDao projectDao, SecurityProvider securityProvider) {

		this.renderer = renderer;
		this.userDao = userDao;
		this.courseDao = courseDao;
		this.projectDao = projectDao;
		this.securityProvider = securityProvider;
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_HTML)
	public String findCourse(@PathParam("id") final long id) {
		final User currentUser = securityProvider.getUser();
		userDao.merge(currentUser);

		final Course course = courseDao.findById(id);
		final List<Project> projects = projectDao.findByCourse(course);

		return renderer
				.setValue("course", course)
				.setValue("projects", projects)
				.addJS("course.js")
				.render("course.tpl");
	}
}
