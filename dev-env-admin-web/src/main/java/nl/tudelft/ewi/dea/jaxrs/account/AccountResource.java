package nl.tudelft.ewi.dea.jaxrs.account;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.tudelft.ewi.dea.dao.RegistrationTokenDao;
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

@Singleton
@Path("account")
public class AccountResource {

	private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

	private final Provider<Renderer> renderers;
	private final RegistrationTokenDao registrationTokenDao;

	@Inject
	public AccountResource(final Provider<Renderer> renderers, final RegistrationTokenDao registrationTokenDao) {
		this.renderers = renderers;
		this.registrationTokenDao = registrationTokenDao;
	}

	@GET
	@Path("activate/{token}")
	@Produces(MediaType.TEXT_HTML)
	public String servePage(@PathParam("token") final String token) {

		LOG.trace("Serving activation page for token: {}", token);

		checkArgument(isNotEmpty(token));

		try {
			registrationTokenDao.findByToken(token);
		} catch (final NoResultException e) {
			LOG.trace("Token not found in database, so not active: {}", token, e);
			// TODO: Render page with 'unknown token' message.
			return renderers.get()
					.setValue("scripts", Lists.newArrayList("activate-unknown-token.js"))
					.render("activate-unknown-token.tpl");
		}

		// TODO: Render page with account input form.
		return renderers.get()
				.setValue("scripts", Lists.newArrayList("activate.js"))
				.render("activate.tpl");
	}

	@POST
	@Path("activate/{token}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response processActivation(@PathParam("token") final String token, final ActivationRequest request) {
		// TODO: check if token is still valid, and account doesn't exist yet.
		// TODO: delete token from database, and create account from request (in
		// single transaction).
		// TODO: automatically log user in, and send a confirmation email.

		final long accountId = 0;

		return Response.seeOther(URI.create("/account/" + accountId)).build();
	}

	@GET
	@Path("email/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findByEmail(@PathParam("email") final String email) {
		// TODO: Return a list of users with a matching email address.
		return Response.serverError().build();
	}

	@POST
	@Path("{id}/promote")
	public Response promoteUserToTeacher(@PathParam("id") final long id) {
		// TODO: Promote user to teacher status.
		return Response.serverError().build();
	}

}
