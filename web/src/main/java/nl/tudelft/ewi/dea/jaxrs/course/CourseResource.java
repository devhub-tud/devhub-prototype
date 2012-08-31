package nl.tudelft.ewi.dea.jaxrs.course;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;
import nl.tudelft.ewi.dea.model.Course;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("course")
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

	private static final Logger LOG = LoggerFactory.getLogger(CourseResource.class);

	private final Provider<Renderer> renderers;

	private final CourseDao courseDao;

	@Inject
	public CourseResource(final Provider<Renderer> renderers, final CourseDao courseDao) {
		this.renderers = renderers;
		this.courseDao = courseDao;
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_HTML)
	public String findCourse(@PathParam("id") final long id) {

		LOG.trace("Finding course: {}", id);

		final Course course = courseDao.findById(id);

		return renderers.get()
				.setValue("course", course)
				.setValue("scripts", Lists.newArrayList("course.js"))
				.render("course.tpl");
	}

}