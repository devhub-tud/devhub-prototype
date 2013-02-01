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
import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("course")
@Produces(MediaType.TEXT_HTML)
public class CoursePage {

	private final Renderer renderer;
	private final CourseDao courseDao;
	private final ProjectDao projectDao;

	@Inject
	public CoursePage(Renderer renderer, CourseDao courseDao, ProjectDao projectDao) {
		this.renderer = renderer;
		this.courseDao = courseDao;
		this.projectDao = projectDao;
	}

	@GET
	@Path("{id}")
	@Transactional
	public String findCourse(@PathParam("id") final long id) {
		final Course course = courseDao.findById(id);
		final List<Project> projects = projectDao.findByCourse(course);

		return renderer
				.setValue("course", course)
				.setValue("projects", projects)
				.addJS("course.js")
				.render("course.tpl");
	}
}
