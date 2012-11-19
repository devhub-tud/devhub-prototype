package nl.tudelft.ewi.dea.jaxrs.html;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("forgot-password")
public class ForgotPasswordPage {

	private final Renderer renderer;

	@Inject
	public ForgotPasswordPage(Renderer renderer) {
		this.renderer = renderer;
	}

	@GET
	public String get() {
		return renderer.render("request-password-reset-mail.tpl");
	}

}
