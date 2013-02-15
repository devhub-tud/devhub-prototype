package nl.tudelft.ewi.dea.jaxrs.api.account;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.SshKeyDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.model.SshKey;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;
import nl.tudelft.ewi.dea.security.SecurityProvider;
import nl.tudelft.ewi.dea.security.UserFactory;
import nl.tudelft.ewi.devhub.services.ServiceException;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;
import nl.tudelft.ewi.devhub.services.versioncontrol.implementations.GitoliteService;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyIdentifier;
import nl.tudelft.ewi.devhub.services.versioncontrol.models.SshKeyRepresentation;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/account")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

	private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

	private final UserDao userDao;
	private final UserFactory userFactory;
	private final SecurityProvider securityProvider;
	private final SshKeyDao keyDao;
	private final VersionControlService localVersioningService;

	@Inject
	public AccountResource(UserDao userDao, UserFactory userFactory,
			SecurityProvider subjectProvider, SshKeyDao keyDao,
			GitoliteService localVersioningService) {

		this.userDao = userDao;
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
		userDao.persist(u);

		return Response.ok().build();
	}

	@POST
	@Path("{id}/demote")
	@RequiresRoles(UserRole.ROLE_ADMIN)
	@Transactional
	public Response demoteTeacherToUser(@PathParam("id") long id) {
		User u = userDao.findById(id);
		u.demoteToUser();
		userDao.persist(u);

		return Response.ok().build();
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
		// TODO: Should we not persist the change?

		return Response.ok(Long.toString(id)).build();
	}

	@POST
	@Path("ssh-keys")
	@Transactional
	public Response addSshKey(SshKeyObject sshKeyObject) {
		User user = securityProvider.getUser();
		SshKey sshKey = null;

		try {
			sshKey = new SshKey(user, sshKeyObject.getName(), sshKeyObject.getKey());
		} catch (IllegalArgumentException e) {
			LOG.error("Invalid SSH key", e);
			return Response.status(Status.CONFLICT).entity("This is not a valid SSH key!").build();
		}

		ServiceUser serviceUser = new ServiceUser(user.getNetId(), user.getDisplayName(), user.getEmail());
		SshKeyIdentifier keyId = new SshKeyIdentifier(sshKey.getKeyName(), serviceUser);
		SshKeyRepresentation key = new SshKeyRepresentation(keyId, sshKey.getKeyContents());

		try {
			localVersioningService.addSshKey(key);
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			return Response.status(Status.CONFLICT).entity("Could not add your SSH key!").build();
		}

		keyDao.persist(sshKey);
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

		ServiceUser serviceUser = new ServiceUser(user.getNetId(), user.getDisplayName(), user.getEmail());
		SshKeyIdentifier[] keyArray = new SshKeyIdentifier[remove.size()];
		for (int i = 0; i < remove.size(); i++) {
			SshKey key = remove.get(i);
			keyArray[i] = new SshKeyIdentifier(key.getKeyName(), serviceUser);
		}

		try {
			localVersioningService.removeSshKeys(keyArray);
		} catch (ServiceException e) {
			LOG.error(e.getMessage(), e);
			return Response.status(Status.CONFLICT).entity("Could not remove your SSH key(s)!").build();
		}

		keyDao.remove(remove.toArray());
		return Response.ok().build();
	}
}
