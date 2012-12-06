package nl.tudelft.ewi.dea.jaxrs.html;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.ServerConfig;
import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;
import nl.tudelft.ewi.dea.mail.DevHubMail;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("feedback")
public class FeedbackPage {

	private final Renderer renderer;
	private final ServerConfig config;
	private final DevHubMail mailer;
	private final SecurityProvider securityProvider;

	@Inject
	public FeedbackPage(Renderer renderer, ServerConfig config,
			SecurityProvider securityProvider, DevHubMail mailer) {

		this.renderer = renderer;
		this.config = config;
		this.securityProvider = securityProvider;
		this.mailer = mailer;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String serveMainPage() {
		return renderer
				.addJS("feedback.js")
				.render("feedback-form.tpl");
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void processFeedback(@FormParam("title") String title, @FormParam("content") String content) {
		User user = securityProvider.getUser();
		String feedbackAddress = config.getFeedbackEmailAddress();
		mailer.sendFeedbackEmail(user.getEmail(), feedbackAddress, title, content);
	}
}
