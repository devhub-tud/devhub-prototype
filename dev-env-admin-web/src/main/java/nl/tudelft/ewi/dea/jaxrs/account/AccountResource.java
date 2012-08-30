package nl.tudelft.ewi.dea.jaxrs.account;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

@Singleton
@Path("account")
public class AccountResource {

	private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

	private final Provider<Renderer> renderers;

	@Inject
	public AccountResource(Provider<Renderer> renderers) {
		this.renderers = renderers;
	}

	@GET
	@Path("activate/{token}")
	@Produces(MediaType.TEXT_HTML)
	public String servePage(@PathParam("token") String token) {
		// TODO: check if token is still valid.

		return renderers.get()
				.setValue("scripts", Lists.newArrayList("activate.js"))
				.render("activate.tpl");
	}

	@POST
	@Path("activate/{token}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response processActivation(@PathParam("token") String token, ActivationRequest request) {
		// TODO: check if token is still valid, and account doesn't exist yet.
		// TODO: delete token from database, and create account from request (in
		// single transaction).
		// TODO: automatically log user in, and send a confirmation email.

		long accountId = 0;

		return Response.seeOther(URI.create("/account/" + accountId)).build();
	}

	public static class ActivationRequest {
		private final String email;
		private final String password;
		private final String displayName;
		private final String netId;
		private final int studentNumber;

		public ActivationRequest() {
			this.email = null;
			this.password = null;
			this.displayName = null;
			this.netId = null;
			this.studentNumber = 0;
		}

		public String getEmail() {
			return email;
		}

		public String getPassword() {
			return password;
		}

		public String getDisplayName() {
			return displayName;
		}

		public String getNetId() {
			return netId;
		}

		public int getStudentNumber() {
			return studentNumber;
		}

		@Override
		public String toString() {
			ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
			builder.append("email", email);
			builder.append("displayName", displayName);
			builder.append("netId", netId);
			builder.append("studentNumber", studentNumber);
			return builder.toString();
		}
	}

}
