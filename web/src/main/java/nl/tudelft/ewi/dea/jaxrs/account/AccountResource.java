package nl.tudelft.ewi.dea.jaxrs.account;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.PasswordResetTokenDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;
import nl.tudelft.ewi.dea.model.PasswordResetToken;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;
import nl.tudelft.ewi.dea.security.UserFactory;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("account")
public class AccountResource {

	private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

	private final Provider<Renderer> renderers;

	private final UserDao userDao;
	private final PasswordResetTokenDao passwordResetTokenDao;

	private final UserFactory userFactory;

	@Inject
	public AccountResource(final Provider<Renderer> renderers, final UserDao userDao, final PasswordResetTokenDao passwordResetTokenDao, final UserFactory userFactory) {
		this.renderers = renderers;
		this.userDao = userDao;
		this.passwordResetTokenDao = passwordResetTokenDao;
		this.userFactory = userFactory;
	}

	@GET
	@Path("{id}")
	@Transactional
	public Response serveAccountPage(@PathParam("id") final long id) {

		LOG.trace("Serving page for account: {} - not yet implemented", id);

		return Response.serverError().entity("Not yet implemented. Go to the <a href=\"/dashboard\">dashboard</a>.").build();

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
			userById = userDao.findById(id);
		} catch (final NoResultException e) {
			LOG.trace("No user found with id: {}", id);
			return Response.status(Status.NOT_FOUND).entity("No user found with id: " + id).build();
		}

		try {
			passwordResetTokenDao.findByToken(token);
		} catch (final NoResultException e) {
			LOG.trace("Token not found: {}", token);
			return Response.status(Status.NOT_FOUND).entity("Token not found: " + token).build();
		}

		final User user = userById;
		final String email = user.getEmail();
		final String displayName = user.getDisplayName();

		final String response = renderers.get()
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
	public Response resetPassword(@PathParam("id") final long id, @PathParam("token") final String token, final PasswordResetRequest request) {

		LOG.trace("Resetting password for id/token: {}/{}", id, token);

		checkArgument(isNotEmpty(token), "token must be a non-empty string");
		checkNotNull(request, "request must be non-null");

		checkArgument(id == request.getId(), "ID in URL path is not equal to ID in request contents");
		checkArgument(token.equals(request.getToken()), "Token in URL path is not equal to token in request contents");

		final PasswordResetToken tokenEntity;
		try {
			tokenEntity = passwordResetTokenDao.findByToken(token);
		} catch (final NoResultException e) {
			LOG.trace("Request token not found: {}", token);
			return Response.status(Status.NOT_FOUND).entity("Token unknown").build();
		}

		final User user = tokenEntity.getUser();
		final String email = user.getEmail();
		final String password = request.getPassword();

		final String userEmail = user.getEmail();

		checkArgument(request.getId() == user.getId(), "ID in request is not equal to user ID");
		checkArgument(email.equals(userEmail), "Email in request is not equal to email stored in database");

		LOG.trace("Resetting user password...");

		userFactory.resetUserPassword(user, password);

		passwordResetTokenDao.remove(tokenEntity);

		LOG.info("Logging user in automatically...");
		SecurityUtils.getSubject().login(new UsernamePasswordToken(email, password));

		return Response.ok(Long.toString(id)).build();

	}

}
