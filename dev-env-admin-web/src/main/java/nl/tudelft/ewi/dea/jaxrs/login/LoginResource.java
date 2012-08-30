package nl.tudelft.ewi.dea.jaxrs.login;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

@Singleton
@Path("login")
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

	private static final Logger LOG = LoggerFactory.getLogger(LoginResource.class);

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

	// @POST
	// @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	// @Produces(MediaType.TEXT_HTML)
	// public Response login(@QueryParam("username") String username,
	// @QueryParam("password") String password, @QueryParam("rememberMe") boolean
	// rememberMe) {
	// LOG.info("Logging in with: " + username + " - " + password);
	//
	// Subject currentUser = SecurityUtils.getSubject();
	// UsernamePasswordToken token = new UsernamePasswordToken(username,
	// password);
	// token.setRememberMe(rememberMe);
	// currentUser.login(token);
	//
	// LOG.info("Redirect to /projects/");
	//
	// return Response.seeOther(URI.create("/projects/")).build();
	// }

}
