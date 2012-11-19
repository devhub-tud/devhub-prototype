package nl.tudelft.ewi.dea.jaxrs.api.account;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.PasswordResetTokenDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.model.PasswordResetToken;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;
import nl.tudelft.ewi.dea.security.SecurityProvider;
import nl.tudelft.ewi.dea.security.UserFactory;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/account")
public class AccountResource {

	private final UserDao userDao;
	private final PasswordResetTokenDao passwordResetTokenDao;
	private final UserFactory userFactory;
	private final SecurityProvider securityProvider;

	@Inject
	public AccountResource(UserDao userDao, PasswordResetTokenDao passwordResetTokenDao,
			UserFactory userFactory, SecurityProvider subjectProvider) {

		this.userDao = userDao;
		this.passwordResetTokenDao = passwordResetTokenDao;
		this.userFactory = userFactory;
		this.securityProvider = subjectProvider;
	}

	@POST
	@Path("{id}/promote")
	@RequiresRoles(UserRole.ROLE_ADMIN)
	public Response promoteUserToTeacher(@PathParam("id") long id) {
		User u = userDao.findById(id);
		u.promoteToAdmin();

		return Response.ok().build();
	}

	@POST
	@Path("{id}/demote")
	@RequiresRoles(UserRole.ROLE_ADMIN)
	public Response demoteTeacherToUser(@PathParam("id") long id) {
		User u = userDao.findById(id);
		u.demoteToUser();

		return Response.ok().build();
	}

	@POST
	@Path("{id}/reset-password/{token}")
	@Transactional
	public Response resetPassword(@PathParam("id") long id, @PathParam("token") String token, PasswordResetRequest request) {
		checkArgument(isNotEmpty(token), "token must be a non-empty string");
		checkNotNull(request, "request must be non-null");
		checkArgument(id == request.getId(), "ID in URL path is not equal to ID in request contents");
		checkArgument(token.equals(request.getToken()), "Token in URL path is not equal to token in request contents");

		PasswordResetToken tokenEntity;
		try {
			tokenEntity = passwordResetTokenDao.findByToken(token);
		} catch (NoResultException e) {
			return Response.status(Status.NOT_FOUND).entity("Token unknown").build();
		}

		User user = tokenEntity.getUser();
		String email = user.getEmail();
		String password = request.getPassword();
		String userEmail = user.getEmail();

		checkArgument(request.getId() == user.getId(), "ID in request is not equal to user ID");
		checkArgument(email.equals(userEmail), "Email in request is not equal to email stored in database");

		userFactory.resetUserPassword(user, password);
		passwordResetTokenDao.remove(tokenEntity);
		SecurityUtils.getSubject().login(new UsernamePasswordToken(email, password));

		return Response.ok(Long.toString(id)).build();
	}

	@POST
	@Path("reset-password")
	public Response resetPassword(NewPasswordRequest request) {
		return resetPassword(securityProvider.getUser().getId(), request);
	}

	@POST
	@Path("{id}/reset-password")
	@Transactional
	public Response resetPassword(@PathParam("id") long id, NewPasswordRequest request) {
		User actingUser = securityProvider.getUser();
		verifyUserIsAdminOrOwnAccount(id, actingUser);

		// We have to get the user from the DAO to make sure we get the
		// persistence instance, not the cached instance.
		final User subject = userDao.findById(id);
		userFactory.resetUserPassword(subject, request.getPassword());

		return Response.ok(Long.toString(id)).build();
	}

	private void verifyUserIsAdminOrOwnAccount(long id, User user) {
		if (!user.isAdmin() && id != user.getId()) {
			throw new AuthorizationException("You can only view your own profile");
		}
	}

}
