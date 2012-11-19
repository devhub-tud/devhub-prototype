package nl.tudelft.ewi.dea.jaxrs.api.register;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/forgot-password")
public class ForgotPasswordResource {

	private final UserDao userDao;
	private final PasswordResetTokenDao passwordResetTokenDao;
	private final String publicUrl;
	private final DevHubMail mail;

	@Inject
	public ForgotPasswordResource(UserDao userDao, PasswordResetTokenDao resetToken,
			ServerConfig serverConfig, DevHubMail mail) {

		this.userDao = userDao;
		this.passwordResetTokenDao = resetToken;
		this.publicUrl = serverConfig.getWebUrl();
		this.mail = mail;
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
		String url = publicUrl + "account/" + id + "/forgot-password/" + token;

		passwordResetTokenDao.persist(new PasswordResetToken(user, token));
		mail.sendResetPasswordMail(email, url);

		return Response.ok().build();
	}
}
