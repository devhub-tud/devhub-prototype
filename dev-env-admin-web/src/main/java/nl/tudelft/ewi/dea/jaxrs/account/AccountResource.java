package nl.tudelft.ewi.dea.jaxrs.account;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;

import com.google.common.collect.Lists;

@Singleton
@Path("account")
public class AccountResource {

	private final Provider<Renderer> renderers;

	@Inject
	public AccountResource(Provider<Renderer> renderers) {
		this.renderers = renderers;
	}

	@GET
	@Path("activate/{token}")
	@Produces(MediaType.TEXT_HTML)
	public String servePage(@PathParam("token") String token) {
		// TODO: check if token is still valid.

		return renderers.get()
				.setValue("scripts", Lists.newArrayList("activate.js"))
				.render("activate.tpl");
	}

	@POST
	@Path("activate/{token}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response processActivation(@PathParam("token") String token, ActivationRequest request) {
		// TODO: check if token is still valid, and account doesn't exist yet.
		// TODO: delete token from database, and create account from request (in
		// single transaction).
		// TODO: automatically log user in, and send a confirmation email.

		long accountId = 0;

		return Response.seeOther(URI.create("/account/" + accountId)).build();
	}

	@GET
	@Path("email/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findByEmail(@PathParam("email") String email) {
		// TODO: Return a list of users with a matching email address.
		return Response.serverError().build();
	}

	@POST
	@Path("{id}/promote")
	public Response promoteUserToTeacher(@PathParam("id") long id) {
		// TODO: Promote user to teacher status.
		return Response.serverError().build();
	}

}
