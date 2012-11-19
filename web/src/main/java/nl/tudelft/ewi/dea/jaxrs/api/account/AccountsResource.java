package nl.tudelft.ewi.dea.jaxrs.api.account;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.RegistrationTokenDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.model.RegistrationToken;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.UserFactory;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;

import com.google.common.base.Preconditions;
import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountsResource {

	private final UserDao userDao;
	private final RegistrationTokenDao registrationTokenDao;
	private final UserFactory userFactory;

	@Inject
	public AccountsResource(UserDao userDao, RegistrationTokenDao registrationTokenDao, UserFactory userFactory) {
		this.userDao = userDao;
		this.registrationTokenDao = registrationTokenDao;
		this.userFactory = userFactory;
	}

	@GET
	public Response findBySubString(@QueryParam("substring") String subString) {
		List<AccountDto> users = AccountDto.convert(userDao.findBySubString(subString));
		GenericEntity<List<AccountDto>> entity = new GenericEntity<List<AccountDto>>(users) {};
		return Response.ok(entity).build();
	}

	@GET
	@Path("email/{email}")
	public Response findByEmailSubString(@PathParam("email") String email) {
		List<AccountDto> users = AccountDto.convert(userDao.findByEmailSubString(email));
		GenericEntity<List<AccountDto>> entity = new GenericEntity<List<AccountDto>>(users) {};
		return Response.ok(entity).build();
	}

	@POST
	@Path("activate/{token}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response processActivation(@PathParam("token") String token, ActivationRequest request) {
		checkArgument(isNotEmpty(token), "token must be a non-empty string");
		checkNotNull(request, "request must be non-null");

		RegistrationToken registrationToken;
		try {
			registrationToken = registrationTokenDao.findByToken(token);
		} catch (NoResultException e) {
			return Response.status(Status.NOT_FOUND).entity("Token is not active").build();
		}

		String email = registrationToken.getEmail();
		Preconditions.checkNotNull(email);

		if (!email.equals(request.getEmail())) {
			return Response.status(Status.BAD_REQUEST).entity("Error: email does not correspond to token").build();
		}

		boolean userExists = true;
		try {
			userDao.findByEmail(email);
		} catch (NoResultException e) {
			userExists = false;
		}

		if (userExists) {
			return Response.serverError().entity("User with email " + email + " already exists.").build();
		}

		String password = request.getPassword();
		User u = userFactory.createUser(email, request.getDisplayName(), request.getNetId(), request.getStudentNumber(), password);

		registrationTokenDao.remove(registrationToken);
		userDao.persist(u);

		SecurityUtils.getSubject().login(new UsernamePasswordToken(email, password));

		// TODO: send a confirmation email.

		long accountId = u.getId();

		return Response.ok(Long.toString(accountId)).build();
	}

}
