package nl.tudelft.ewi.dea.jaxrs.html;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.BuildInfo;
import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("login")
public class LoginPage {

	private final Renderer renderer;
	private final BuildInfo buildInfo;

	@Inject
	public LoginPage(Renderer renderer, BuildInfo buildInfo) {
		this.renderer = renderer;
		this.buildInfo = buildInfo;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String servePage() {
		return renderer
				.setValue("buildInfo", buildInfo)
				.render("login.tpl");
	}

}
