package nl.tudelft.ewi.dea.jaxrs.register;

import java.net.URI;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Path("register")
@Produces(MediaType.APPLICATION_JSON)
public class RegisterResource {

	private static final Logger LOG = LoggerFactory.getLogger(RegisterResource.class);

	private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";

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
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response processSignupRequest(SignupRequest request) {
		// TODO: Implement this.
		LOG.info("Request: {}", request);
		// return Response.serverError().entity("Hello world").build();
		return Response.ok().build();
	}

	@GET
	@Path("checkEmail")
	public Response checkEmail(@QueryParam("email") String email) {
		if (!email.matches(EMAIL_REGEX)) {
			return Response.status(Status.CONFLICT).entity("The provided email address is not valid!").build();
		}

		if (!isAllowedDomain(email)) {
			return Response.status(Status.CONFLICT).entity("You must enter a valid tudelft email address!").build();
		}

		// TODO: Check if account is already registered.
		return Response.ok().build();
	}

	// TODO: Move this to config file...
	private boolean isAllowedDomain(String email) {
		return email.endsWith("@tudelft.nl") || email.endsWith("@student.tudelft.nl");
	}
}
