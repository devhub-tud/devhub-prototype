package nl.tudelft.ewi.dea.jaxrs.html;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.RegistrationTokenDao;
import nl.tudelft.ewi.dea.dao.SshKeyDao;
import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;
import nl.tudelft.ewi.dea.model.RegistrationToken;
import nl.tudelft.ewi.dea.model.SshKey;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@Path("account")
@RequestScoped
@Produces(MediaType.TEXT_HTML)
public class AccountPage {

	private static final Logger LOG = LoggerFactory.getLogger(AccountPage.class);

	private final Renderer renderer;
	private final RegistrationTokenDao tokenDao;
	private final SshKeyDao keyDao;
	private final SecurityProvider securityProvider;

	@Inject
	public AccountPage(Renderer renderer, SecurityProvider securityProvider,
			RegistrationTokenDao tokenDao, ProjectDao projectDao, SshKeyDao keyDao) {

		this.renderer = renderer;
		this.securityProvider = securityProvider;
		this.tokenDao = tokenDao;
		this.keyDao = keyDao;
	}

	@GET
	public Response redirectToAccountDetails() throws URISyntaxException {
		return Response.seeOther(new URI("/account/details")).build();
	}

	@GET
	@Path("activate/{token}")
	@Transactional
	public String serveActivationPage(@PathParam("token") final String token) {
		Preconditions.checkArgument(StringUtils.isNotEmpty(token), "token must be a non-empty string");

		RegistrationToken tokenObject = null;
		try {
			tokenObject = tokenDao.findByToken(token);
		} catch (final NoResultException e) {
			LOG.trace("Token not found in database, so not active: {}", token, e);
			return renderer.render("activate-unknown-token.tpl");
		}

		return renderer
				.setValue("email", tokenObject.getEmail())
				.render("activate.tpl");
	}

	@GET
	@Path("details")
	@Transactional
	public String serveAccountDetailsPage() {
		return renderer.render("account.tpl", "account-details.tpl");
	}

	@GET
	@Path("ssh-keys")
	@Transactional
	public String serveAccountSshKeyManagementPage() {
		User user = securityProvider.getUser();
		List<SshKey> keys = keyDao.list(user);

		return renderer
				.setValue("ssh-keys", keys)
				.addJS("account-ssh-keys.js")
				.render("account.tpl", "account-ssh-keys.tpl");
	}

	@GET
	@Path("change-password")
	@Transactional
	public String serveAccountChangePasswordPage() {
		return renderer
				.addJS("account-change-password.js")
				.render("account.tpl", "account-change-password.tpl");
	}

}
