package nl.tudelft.ewi.dea.jaxrs.html;

import java.util.Properties;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.ServerConfig;
import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("support")
@Produces(MediaType.TEXT_HTML)
public class SupportPage {

	private final Renderer renderer;
	private final ServerConfig config;

	@Inject
	public SupportPage(Renderer renderer, ServerConfig config) {
		this.renderer = renderer;
		this.config = config;
	}

	@GET
	@Transactional
	public String serveMainPage() {
		return renderer.render("support-main.tpl");
	}

	@GET
	@Path("ssh-key-management")
	@Transactional
	public String serveSshKeyManagementSupportPage() {
		Properties gitoliteSettings = config.getServices().get("versionControl").get("Gitolite");
		String gitHost = gitoliteSettings.getProperty("host");
		String gitUser = gitoliteSettings.getProperty("user");

		return renderer
				.setValue("gitUser", gitUser)
				.setValue("gitHost", gitHost)
				.addJS("animate-colors.js")
				.addJS("support.js")
				.render("support-ssh-key-management.tpl");
	}

}
