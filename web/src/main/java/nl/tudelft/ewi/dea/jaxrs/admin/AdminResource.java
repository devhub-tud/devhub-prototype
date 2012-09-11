package nl.tudelft.ewi.dea.jaxrs.admin;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.UserRole;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

	private static final Logger LOG = LoggerFactory.getLogger(AdminResource.class);

	private final Provider<Renderer> renderers;

	private final CourseDao courseDao;

	@Inject
	public AdminResource(final Provider<Renderer> renderers, final CourseDao courseDao) {
		this.renderers = renderers;

		this.courseDao = courseDao;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@RequiresRoles(UserRole.ROLE_ADMIN)
	public String servePage() {
		LOG.trace("Serving admin dashboard.");

		final List<Course> courses = courseDao.findAll();

		return renderers.get()
				.setValue("courses", courses)
				.setValue("scripts", Lists.newArrayList("admin-create-course.js", "admin-promote-user.js"))
				.render("admin.tpl");
	}

}
