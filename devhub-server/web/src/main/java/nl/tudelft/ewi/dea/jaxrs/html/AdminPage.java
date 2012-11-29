package nl.tudelft.ewi.dea.jaxrs.html;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.UserRole;

import org.apache.shiro.authz.annotation.RequiresRoles;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("admin")
@Produces(MediaType.TEXT_HTML)
public class AdminPage {

	private final Renderer renderer;
	private final CourseDao courseDao;

	@Inject
	public AdminPage(Renderer renderer, CourseDao courseDao) {
		this.renderer = renderer;
		this.courseDao = courseDao;
	}

	@GET
	@Transactional
	@RequiresRoles(UserRole.ROLE_ADMIN)
	public String servePage() {
		final List<Course> courses = courseDao.findAll();

		return renderer
				.setValue("courses", courses)
				.addJS("admin-create-course.js")
				.addJS("admin-promote-user.js")
				.render("admin.tpl");
	}

}
