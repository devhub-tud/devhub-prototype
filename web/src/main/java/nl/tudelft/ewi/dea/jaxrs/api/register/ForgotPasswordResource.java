package nl.tudelft.ewi.dea.jaxrs.api.register;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.net.URISyntaxException;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.ServerConfig;
import nl.tudelft.ewi.dea.dao.PasswordResetTokenDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.mail.DevHubMail;
import nl.tudelft.ewi.dea.model.PasswordResetToken;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.UserFactory;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/forgot-password")
public class ForgotPasswordResource {

	private final UserDao userDao;
	private final PasswordResetTokenDao passwordResetTokenDao;
	private final String publicUrl;
	private final DevHubMail mail;
	private final UserFactory userFactory;

	@Inject
	public ForgotPasswordResource(UserDao userDao, PasswordResetTokenDao resetToken,
			UserFactory userFactory, ServerConfig serverConfig, DevHubMail mail) {

		this.userDao = userDao;
		this.passwordResetTokenDao = resetToken;
		this.userFactory = userFactory;
		this.publicUrl = serverConfig.getWebUrl();
		this.mail = mail;
	}

	@POST
	@Path("{id}/{token}")
	@Transactional
	public Response resetPassword(@PathParam("id") long id, @PathParam("token") String token,
			PasswordResetRequest request) throws URISyntaxException {

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
		userDao.persist(user);

		passwordResetTokenDao.remove(tokenEntity);

		SecurityUtils.getSubject().login(new UsernamePasswordToken(email, password));
		return Response.ok(Long.toString(id)).build();
	}

	@POST
	@Path("{email}")
	@Transactional
	public Response sendPasswordResetEmail(@PathParam("email") String email) {
		checkArgument(isNotEmpty(email), "email must be a non-empty string");

		User user;
		try {
			user = userDao.findByEmail(email);
		} catch (final NoResultException e) {
			return Response.status(Status.NOT_FOUND).build();
		}

		boolean tokenExists = true;
		try {
			passwordResetTokenDao.findByEmail(email);
		} catch (final NoResultException e) {
			tokenExists = false;
		}

		if (tokenExists) {
			return Response.status(Status.CONFLICT).entity("Token email was already sent").build();
		}

		long id = user.getId();
		String token = UUID.randomUUID().toString();
		String url = publicUrl + "/forgot-password/" + id + "/" + token;

		passwordResetTokenDao.persist(new PasswordResetToken(user, token));
		mail.sendResetPasswordMail(email, url);

		return Response.ok().build();
	}
}
