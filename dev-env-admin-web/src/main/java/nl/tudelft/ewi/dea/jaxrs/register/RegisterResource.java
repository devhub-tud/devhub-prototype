package nl.tudelft.ewi.dea.jaxrs.register;

import java.net.URI;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Path("register")
@Produces(MediaType.APPLICATION_JSON)
public class RegisterResource {

	private static final Logger LOG = LoggerFactory.getLogger(RegisterResource.class);

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
	// TODO: Implement this.
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response processSignupRequest(SignupRequest request) {
		LOG.info("Request: {}", request);
		return Response.ok().build();
		// return Response.serverError().entity("Hello world").build();
	}

	public static class SignupRequest {
		private final String email;

		public SignupRequest() {
			this.email = null;
		}

		public String getEmail() {
			return email;
		}

		@Override
		public String toString() {
			ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
			builder.append("email", email);
			return builder.toString();
		}
	}

}
