package nl.tudelft.ewi.dea.jaxrs.login;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.BuildInfo;
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("login")
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

	private final Provider<Renderer> renderers;
	private final BuildInfo buildInfo;

	@Inject
	public LoginResource(Provider<Renderer> renderers, BuildInfo buildInfo) {
		this.renderers = renderers;
		this.buildInfo = buildInfo;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String servePage() {
		return renderers.get()
				.setValue("buildInfo", buildInfo)
				.render("login.tpl");
	}

}
