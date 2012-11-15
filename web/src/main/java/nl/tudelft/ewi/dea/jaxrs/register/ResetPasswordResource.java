package nl.tudelft.ewi.dea.jaxrs.register;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.ServerConfig;
import nl.tudelft.ewi.dea.dao.PasswordResetTokenDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;
import nl.tudelft.ewi.dea.mail.DevHubMail;
import nl.tudelft.ewi.dea.model.PasswordResetToken;
import nl.tudelft.ewi.dea.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("reset-password")
public class ResetPasswordResource {

	private static final Logger LOG = LoggerFactory.getLogger(ResetPasswordResource.class);

	private final Provider<Renderer> renderers;

	private final UserDao userDao;
	private final PasswordResetTokenDao passwordResetTokenDao;

	private final String publicUrl;

	private final DevHubMail mail;

	@Inject
	public ResetPasswordResource(final Provider<Renderer> renderers, final UserDao userDao, final PasswordResetTokenDao passwordResetTokenDao, ServerConfig serverConfig, final DevHubMail mail) {
		this.renderers = renderers;

		this.userDao = userDao;
		this.passwordResetTokenDao = passwordResetTokenDao;
		this.publicUrl = serverConfig.getWebUrl();
		this.mail = mail;
	}

	@GET
	public String get() {

		LOG.trace("Get: Password reset page");

		return renderers.get()
				.render("request-password-reset-mail.tpl");

	}

	@POST
	@Path("{email}")
	@Transactional
	public Response sendPasswordResetEmail(@PathParam("email") final String email) {

		LOG.trace("Recovering password for email: {}", email);

		checkArgument(isNotEmpty(email), "email must be a non-empty string");

		User user;
		try {
			user = userDao.findByEmail(email);
		} catch (final NoResultException e) {
			LOG.trace("No user with email: {}", email);
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

		final long id = user.getId();
		final String token = UUID.randomUUID().toString();

		passwordResetTokenDao.persist(new PasswordResetToken(user, token));

		final String url = publicUrl + "account/" + id + "/reset-password/" + token;

		mail.sendResetPasswordMail(email, url);

		return Response.ok().build();

	}
}
