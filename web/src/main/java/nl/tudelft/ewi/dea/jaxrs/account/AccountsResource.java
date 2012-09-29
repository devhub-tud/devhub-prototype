package nl.tudelft.ewi.dea.jaxrs.account;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.RegistrationTokenDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;
import nl.tudelft.ewi.dea.model.RegistrationToken;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.UserFactory;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("accounts")
public class AccountsResource {

	private static final Logger LOG = LoggerFactory.getLogger(AccountsResource.class);

	private final Provider<Renderer> renderers;

	private final UserDao userDao;
	private final RegistrationTokenDao registrationTokenDao;

	private final UserFactory userFactory;

	@Inject
	public AccountsResource(final Provider<Renderer> renderers, final UserDao userDao, final RegistrationTokenDao registrationTokenDao, final UserFactory userFactory) {
		this.renderers = renderers;
		this.userDao = userDao;
		this.registrationTokenDao = registrationTokenDao;
		this.userFactory = userFactory;
	}

	@GET
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public Response findBySubString(@QueryParam("substring") final String subString) {
		LOG.trace("Find by substring: {}", subString);
		final List<Account> users = Account.convert(userDao.findBySubString(subString));
		GenericEntity<List<Account>> entity = new GenericEntity<List<Account>>(users) {};
		return Response.ok(entity).build();
	}

	@GET
	@Path("email/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> findByEmailSubString(@PathParam("email") final String email) {

		LOG.trace("Find accounts by email: {}", email);

		final List<User> users = userDao.findByEmailSubString(email);

		return users;

	}

	@GET
	@Path("activate/{token}")
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public String serveActivationPage(@PathParam("token") final String token) {

		LOG.trace("Serving activation page for token: {}", token);

		checkArgument(isNotEmpty(token), "token must be a non-empty string");

		RegistrationToken tokenObject = null;
		try {
			tokenObject = registrationTokenDao.findByToken(token);
		} catch (final NoResultException e) {
			LOG.trace("Token not found in database, so not active: {}", token, e);
			return renderers.get().render("activate-unknown-token.tpl");
		}

		return renderers.get()
				.setValue("email", tokenObject.getEmail())
				.render("activate.tpl");
	}

	@POST
	@Path("activate/{token}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response processActivation(@PathParam("token") final String token, final ActivationRequest request) {

		LOG.trace("Processing activation with token {} and request {}", token, request);

		checkArgument(isNotEmpty(token), "token must be a non-empty string");
		checkNotNull(request, "request must be non-null");

		RegistrationToken registrationToken;
		try {
			registrationToken = registrationTokenDao.findByToken(token);
		} catch (final NoResultException e) {
			LOG.trace("Token not found in database, so not active: {}", token, e);
			return Response.status(Status.NOT_FOUND).entity("Token is not active").build();
		}

		final String email = registrationToken.getEmail();
		assert email != null;

		if (!email.equals(request.getEmail())) {
			LOG.warn("Email {} does not correspond to registration token {}", email, token);
			return Response.status(Status.BAD_REQUEST).entity("Error: email does not correspond to token").build();
		}

		boolean userExists = true;
		try {
			userDao.findByEmail(email);
		} catch (final NoResultException e) {
			LOG.trace("No user found with email: {}", email);
			userExists = false;
		}

		if (userExists) {
			LOG.warn("User already exists: {}", email);
			return Response.serverError().entity("User with email " + email + " already exists.").build();
		}

		final String password = request.getPassword();
		final User u = userFactory.createUser(email, request.getDisplayName(), request.getNetId(), request.getStudentNumber(), password);

		registrationTokenDao.remove(registrationToken);
		userDao.persist(u);

		LOG.trace("Logging in automatically as user: {}", email);
		SecurityUtils.getSubject().login(new UsernamePasswordToken(email, password));

		// TODO: send a confirmation email.

		final long accountId = u.getId();

		LOG.debug("Created account with id: " + accountId);
		return Response.ok(Long.toString(accountId)).build();

	}

}
