package nl.tudelft.ewi.dea.jaxrs.api.account;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.PasswordResetTokenDao;
import nl.tudelft.ewi.dea.dao.SshKeyDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.model.PasswordResetToken;
import nl.tudelft.ewi.dea.model.SshKey;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;
import nl.tudelft.ewi.dea.security.SecurityProvider;
import nl.tudelft.ewi.dea.security.UserFactory;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;
import nl.tudelft.ewi.devhub.services.versioncontrol.implementations.GitoliteService;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyRepresentation;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresRoles;

import com.google.common.collect.Lists;
import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/account")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

	private final UserDao userDao;
	private final PasswordResetTokenDao passwordResetTokenDao;
	private final UserFactory userFactory;
	private final SecurityProvider securityProvider;
	private final SshKeyDao keyDao;
	private final VersionControlService localVersioningService;

	@Inject
	public AccountResource(UserDao userDao, PasswordResetTokenDao passwordResetTokenDao,
			UserFactory userFactory, SecurityProvider subjectProvider, SshKeyDao keyDao,
			GitoliteService localVersioningService) {

		this.userDao = userDao;
		this.passwordResetTokenDao = passwordResetTokenDao;
		this.userFactory = userFactory;
		this.securityProvider = subjectProvider;
		this.keyDao = keyDao;
		this.localVersioningService = localVersioningService;
	}

	@POST
	@Path("{id}/promote")
	@RequiresRoles(UserRole.ROLE_ADMIN)
	@Transactional
	public Response promoteUserToTeacher(@PathParam("id") long id) {
		User u = userDao.findById(id);
		u.promoteToAdmin();
		userDao.merge(u);

		return Response.ok().build();
	}

	@POST
	@Path("{id}/demote")
	@RequiresRoles(UserRole.ROLE_ADMIN)
	@Transactional
	public Response demoteTeacherToUser(@PathParam("id") long id) {
		User u = userDao.findById(id);
		u.demoteToUser();
		userDao.merge(u);

		return Response.ok().build();
	}

	@POST
	@Path("{id}/reset-password/{token}")
	@Transactional
	public Response resetPassword(@PathParam("id") long id, @PathParam("token") String token, PasswordResetRequest request) {
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
		passwordResetTokenDao.remove(tokenEntity);
		SecurityUtils.getSubject().login(new UsernamePasswordToken(email, password));

		return Response.ok(Long.toString(id)).build();
	}

	@POST
	@Path("reset-password")
	public Response resetPassword(NewPasswordRequest request) {
		return resetPassword(securityProvider.getUser().getId(), request);
	}

	@POST
	@Path("{id}/reset-password")
	@Transactional
	public Response resetPassword(@PathParam("id") long id, NewPasswordRequest request) {
		User user = securityProvider.getUser();
		if (!user.isAdmin() && id != user.getId()) {
			throw new AuthorizationException("You can only view your own profile");
		}

		// We have to get the user from the DAO to make sure we get the
		// persistence instance, not the cached instance.
		final User subject = userDao.findById(id);
		userFactory.resetUserPassword(subject, request.getPassword());

		return Response.ok(Long.toString(id)).build();
	}

	@POST
	@Path("ssh-keys")
	@Transactional
	public Response addSshKey(SshKeyObject sshKeyObject) {
		User user = securityProvider.getUser();
		SshKey sshKey = new SshKey(user, sshKeyObject.getName(), sshKeyObject.getKey());
		keyDao.persist(sshKey);

		ServiceUser serviceUser = new ServiceUser(user.getNetId(), user.getEmail());
		SshKeyIdentifier keyId = new SshKeyIdentifier(sshKey.getKeyName(), serviceUser);
		SshKeyRepresentation key = new SshKeyRepresentation(keyId, sshKey.getKeyContents());
		localVersioningService.addSshKey(key);

		return Response.ok().build();
	}

	@DELETE
	@Path("ssh-keys")
	@Transactional
	public Response removeSshKey(SshKeyDeleteObject sshKeys) {
		User user = securityProvider.getUser();
		List<SshKey> keys = keyDao.list(user);
		List<SshKey> remove = Lists.newArrayList();

		for (long keyId : sshKeys.getKeyIds()) {
			for (SshKey key : keys) {
				if (key.getId() == keyId) {
					remove.add(key);
					break;
				}
			}
		}

		keyDao.remove(remove.toArray());

		ServiceUser serviceUser = new ServiceUser(user.getNetId(), user.getEmail());
		for (SshKey key : remove) {
			localVersioningService.removeSshKeys(new SshKeyIdentifier(key.getKeyName(), serviceUser));
		}

		return Response.ok().build();
	}
}
