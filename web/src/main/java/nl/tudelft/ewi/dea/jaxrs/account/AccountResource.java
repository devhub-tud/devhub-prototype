package nl.tudelft.ewi.dea.jaxrs.account;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.mail.PasswordAuthentication;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.PasswordResetTokenDao;
import nl.tudelft.ewi.dea.dao.RegistrationTokenDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;
import nl.tudelft.ewi.dea.model.PasswordResetToken;
import nl.tudelft.ewi.dea.model.RegistrationToken;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;
import nl.tudelft.ewi.dea.security.UserFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("account")
public class AccountResource {

	private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

	private final Provider<Renderer> renderers;

	private final UserDao userDao;
	private final RegistrationTokenDao registrationTokenDao;
	private PasswordResetTokenDao passwordResetTokenDao;

	private final UserFactory userFactory;

	@Inject
	public AccountResource(final Provider<Renderer> renderers, final UserDao userDao, final RegistrationTokenDao registrationTokenDao, final PasswordResetTokenDao passwordResetTokenDao, final UserFactory userFactory) {
		this.renderers = renderers;
		this.userDao = userDao;
		this.registrationTokenDao = registrationTokenDao;
		this.passwordResetTokenDao = passwordResetTokenDao;
		this.userFactory = userFactory;
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

		// TODO: Log in the user automatically.
		// SecurityUtils.getSubject().login(new UsernamePasswordToken(email,
		// password));

		// TODO: send a confirmation email.

		final long accountId = u.getId();

		LOG.debug("Created account with id: " + accountId);
		return Response.ok(Long.toString(accountId)).build();

	}

	@GET
	@Path("email/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> findByEmail(@PathParam("email") final String email) {

		LOG.trace("Find accounts by email: {}", email);

		final List<User> users = userDao.findByEmailSubString(email);

		return users;

	}

	@POST
	@Path("{id}/promote")
	@RequiresRoles(UserRole.ROLE_ADMIN)
	@Transactional
	public Response promoteUserToTeacher(@PathParam("id") final long id) {

		LOG.trace("Promote user to admin: {}", id);

		final User u = userDao.findById(id);
		u.makeAdmin();

		return Response.ok().build();

	}

	@GET
	@Path("{id}/reset-password/{token}")
	@Transactional
	public Response serveResetPasswordPage(@PathParam("id") final long id, @PathParam("token") final String token) {

		LOG.trace("Serving password reset page for account/token: {}/{}", id, token);

		final User userById;
		try {
			userById = this.userDao.findById(id);
		} catch (NoResultException e) {
			LOG.trace("No user found with id: {}", id);
			return Response.status(Status.NOT_FOUND).entity("No user found with id: " + id).build();
		}

		final User user = userById;
		final String email = user.getEmail();
		final String displayName = user.getDisplayName();

		String response = renderers.get()
				.setValue("id", id)
				.setValue("token", token)
				.setValue("email", email)
				.setValue("displayName", displayName)
				.render("reset-account-password.tpl");

		return Response.ok(response).build();

	}

	@POST
	@Path("{id}/reset-password/{token}")
	@Transactional
	public Response resetPassword(@PathParam("id") final long id, @PathParam("token") final String token, PasswordResetRequest request) {

		LOG.info("Resetting password for id/token: {}/{}", id, token);

		checkArgument(isNotEmpty(token), "token must be a non-empty string");
		checkNotNull(request, "request must be non-null");

		checkArgument(id == request.getId(), "ID in URL path is not equal to ID in request contents");
		checkArgument(token.equals(request.getToken()), "Token in URL path is not equal to token in request contents");

		final PasswordResetToken tokenEntity;
		try {
			tokenEntity = this.passwordResetTokenDao.findByToken(token);
		} catch (NoResultException e) {
			LOG.warn("Request token not found: {}", token);
			return Response.status(Status.NOT_FOUND).entity("Token unknown").build();
		}

		final User user = tokenEntity.getUser();
		final String email = user.getEmail();
		String password = request.getPassword();

		final String userEmail = user.getEmail();

		checkArgument(request.getId() == user.getId(), "ID in request is not equal to user ID");
		checkArgument(email.equals(userEmail), "Email in request is not equal to email stored in database");

		LOG.info("Resetting user password...");

		userFactory.resetUserPassword(user, password);

		passwordResetTokenDao.remove(tokenEntity);

		// TODO: Automatically login the user.
		// SecurityUtils.getSubject().login(new UsernamePasswordToken(email,
		// password));

		return Response.ok(Long.toString(id)).build();

	}

}
