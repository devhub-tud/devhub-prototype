package nl.tudelft.ewi.dea.jaxrs.api.projects;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.ServerConfig;
import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectInvitationDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.mail.DevHubMail;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/project")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResource {

	private final SecurityProvider securityProvider;
	private final UserDao userDao;
	private final ProjectDao projectDao;
	private final ProjectInvitationDao invitationDao;
	private final ProjectMembershipDao membershipDao;
	private final DevHubMail mail;
	private final String publicUrl;

	@Inject
	public ProjectResource(SecurityProvider securityProvider, ProjectDao projectDao,
			UserDao userDao, ProjectInvitationDao invitationDao, ProjectMembershipDao membershipDao, DevHubMail mail,
			ServerConfig serverConfig) {

		this.userDao = userDao;
		this.projectDao = projectDao;
		this.invitationDao = invitationDao;
		this.membershipDao = membershipDao;
		this.securityProvider = securityProvider;
		this.publicUrl = serverConfig.getWebUrl();
		this.mail = mail;
	}

	@GET
	@Path("{projectId}/invite/{userMail}")
	public Response inviteUser(@PathParam("projectId") long projectId, @PathParam("userMail") String email) {
		Project project = projectDao.findById(projectId);
		User otherUser;
		try {
			otherUser = userDao.findByEmail(email);
		} catch (NoResultException e) {
			return Response.status(Status.CONFLICT).entity("unknown-user").build();
		}

		if (userIsAlreadyInvited(project, otherUser)) {
			return Response.status(Status.CONFLICT)
					.entity("User is already invited")
					.build();
		}

		ProjectInvitation invitation = new ProjectInvitation(otherUser, project);
		invitationDao.persist(invitation);

		String fromName = otherUser.getDisplayName();
		if (fromName == null || fromName.isEmpty()) {
			fromName = otherUser.getEmail();
		}

		mail.sendProjectInvite(otherUser.getEmail(), fromName, project.getName(), publicUrl);
		return Response.ok().build();
	}

	@GET
	@Path("{id}/invitation")
	public Response answerInvitation(@PathParam("id") long id, @QueryParam("accept") boolean accept) {
		User currentUser = securityProvider.getUser();
		Project project = projectDao.findById(id);
		ProjectInvitation invitation = invitationDao.findByProjectAndUser(project, currentUser);

		if (accept) {
			final ProjectMembership membership = new ProjectMembership(currentUser, project);
			membershipDao.persist(membership);
		}

		invitationDao.remove(invitation);
		return Response.ok().build();
	}

	private boolean userIsAlreadyInvited(final Project project, final User user) {
		try {
			invitationDao.findByProjectAndUser(project, user);
			return true;
		} catch (final NoResultException e) {
			return false;
		}
	}

}
