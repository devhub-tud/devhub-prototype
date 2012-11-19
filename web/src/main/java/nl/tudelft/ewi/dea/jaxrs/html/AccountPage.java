package nl.tudelft.ewi.dea.jaxrs.html;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;

@Path("account")
public class AccountPage {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response redirectToAccountDetails() throws URISyntaxException {
		return Response.seeOther(new URI("/account/details")).build();
	}

	@Path("account/details")
	public static class AccountDetailsPage {

		private final Renderer renderer;

		@Inject
		public AccountDetailsPage(Renderer renderer) {
			this.renderer = renderer;
		}

		@GET
		@Produces(MediaType.TEXT_HTML)
		public String servePage() {
			return renderer.render("account.tpl", "account-details.tpl");
		}
	}

	@Path("account/ssh-keys")
	public static class AccountSshKeyManagementPage {

		private final Renderer renderer;

		@Inject
		public AccountSshKeyManagementPage(Renderer renderer) {
			this.renderer = renderer;
		}

		@GET
		@Produces(MediaType.TEXT_HTML)
		public String servePage() {
			return renderer
					.addJS("account-ssh-keys.js")
					.render("account.tpl", "account-ssh-keys.tpl");
		}
	}

	@Path("account/change-password")
	public static class AccountChangePasswordPage {

		private final Renderer renderer;

		@Inject
		public AccountChangePasswordPage(Renderer renderer) {
			this.renderer = renderer;
		}

		@GET
		@Produces(MediaType.TEXT_HTML)
		public String servePage() {
			return renderer
					.addJS("account-change-password.js")
					.render("account.tpl", "account-change-password.tpl");
		}
	}

}
