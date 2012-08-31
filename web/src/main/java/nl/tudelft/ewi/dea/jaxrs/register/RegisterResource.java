package nl.tudelft.ewi.dea.jaxrs.register;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.RegistrationTokenDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.mail.DevHubMail;
import nl.tudelft.ewi.dea.model.RegistrationToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("register")
@Produces(MediaType.APPLICATION_JSON)
public class RegisterResource {

	private static final Logger LOG = LoggerFactory.getLogger(RegisterResource.class);

	private final UserDao userDao;
	private final RegistrationTokenDao tokenDao;
	private final DevHubMail mailer;
	private final String publicUrl;

	@Inject
	public RegisterResource(UserDao userDao, RegistrationTokenDao tokenDao, DevHubMail mailer,
			@Named("webapp.public-url") String publicUrl) {
		this.userDao = userDao;
		this.tokenDao = tokenDao;
		this.mailer = mailer;
		this.publicUrl = publicUrl;
	}

	@GET
	public Response servePage() {
		return Response.seeOther(URI.create("/login")).build();
	}

	/**
	 * <pre>
	 * -- Sign up and wait for confirmation email
	 * Given
	 * 	I have not logged in to the system,
	 * When
	 * 	I am presented with the signup screen, and
	 * 	I click on 'sign up', and
	 * 	I supply my TUD email, and
	 * 	I click on 'sign up',
	 * Then
	 * 	I am presented with a screen that tells me to check my email, and
	 * 	I receive an email on that account, which contains a one-time valid URL to my account page
	 * 		(e.g. http://dea.hartveld.com/devhub/account/{accountId}/{one-time-valid-auth-token}).
	 * </pre>
	 * 
	 * @return The response of this request.
	 * 
	 * @throws UnknownHostException If the server is unable to resolve its own
	 *            hostname.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response processSignupRequest(SignupRequest request) throws UnknownHostException {
		LOG.info("Request: {}", request);

		if (emailAlreadyRegistered(request.getEmail())) {
			return Response.status(Status.CONFLICT).entity("This e-mail address is already registered!").build();
		}

		if (registrationTokenAlreadyRequested(request.getEmail())) {
			return Response.status(Status.CONFLICT).entity("This e-mail address is already registered!").build();
		}

		String token = UUID.randomUUID().toString();
		tokenDao.persist(new RegistrationToken(request.getEmail(), token));

		String verifyUrl = publicUrl + "account/activate/" + token;
		mailer.sendVerifyRegistrationMail(request.getEmail(), verifyUrl);

		return Response.ok().build();
	}

	private boolean emailAlreadyRegistered(String email) {
		try {
			return userDao.findByEmail(email) != null;
		} catch (NoResultException e) {
			return false;
		}
	}

	private boolean registrationTokenAlreadyRequested(String email) {
		try {
			return tokenDao.findByEmail(email) != null;
		} catch (NoResultException e) {
			return false;
		}
	}

}
