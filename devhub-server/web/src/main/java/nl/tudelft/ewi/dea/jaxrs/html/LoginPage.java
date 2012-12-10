package nl.tudelft.ewi.dea.jaxrs.html;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.tudelft.ewi.dea.BuildInfo;
import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("login")
@Produces(MediaType.TEXT_HTML)
public class LoginPage {

	private final Renderer renderer;
	private final BuildInfo buildInfo;
	private final SecurityProvider securityProvider;

	@Inject
	public LoginPage(SecurityProvider securityProvider,
			Renderer renderer, BuildInfo buildInfo) {

		this.securityProvider = securityProvider;
		this.renderer = renderer;
		this.buildInfo = buildInfo;
	}

	@GET
	@Transactional
	public Response servePage() {
		if (securityProvider.getSubject().isAuthenticated()) {
			return Response.seeOther(URI.create("/dashboard")).build();
		}

		return Response.ok(renderer
				.setValue("buildInfo", buildInfo)
				.render("login.tpl")).build();
	}
}
