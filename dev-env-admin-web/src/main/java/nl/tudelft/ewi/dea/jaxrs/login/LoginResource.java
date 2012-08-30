package nl.tudelft.ewi.dea.jaxrs.login;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;

import com.google.common.collect.Lists;

@Singleton
@Path("login")
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

	private final Provider<Renderer> renderers;

	@Inject
	public LoginResource(Provider<Renderer> renderers) {
		this.renderers = renderers;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String servePage() {
		return renderers.get()
				.setValue("scripts", Lists.newArrayList("login.js"))
				.render("login.tpl");
	}

}
