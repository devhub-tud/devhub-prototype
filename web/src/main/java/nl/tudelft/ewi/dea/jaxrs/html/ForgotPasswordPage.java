package nl.tudelft.ewi.dea.jaxrs.html;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;
import nl.tudelft.ewi.dea.model.User;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("forgot-password")
@Produces(MediaType.TEXT_HTML)
public class ForgotPasswordPage {

	private final Renderer renderer;
	private final UserDao userDao;

	@Inject
	public ForgotPasswordPage(Renderer renderer, UserDao userDao) {
		this.renderer = renderer;
		this.userDao = userDao;
	}

	@GET
	@Transactional
	public String serveRequestPage() {
		return renderer.render("request-password-reset-mail.tpl");
	}

	@GET
	@Transactional
	@Path("{id}/{token}")
	public String serveCompletionPage(@PathParam("id") long id, @PathParam("token") String token) {
		User user = userDao.findById(id);

		return renderer
				.setValue("id", id)
				.setValue("token", token)
				.setValue("email", user.getEmail())
				.setValue("displayName", user.getDisplayName())
				.render("reset-account-password.tpl");
	}
}
