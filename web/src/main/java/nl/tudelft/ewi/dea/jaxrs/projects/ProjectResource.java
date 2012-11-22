package nl.tudelft.ewi.dea.jaxrs.projects;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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
import nl.tudelft.ewi.dea.dao.RegistrationTokenDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;
import nl.tudelft.ewi.dea.mail.DevHubMail;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.RegistrationToken;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("project")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResource {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectResource.class);

	private final SecurityProvider securityProvider;

	private final UserDao userDao;
	private final ProjectDao projectDao;
	private final ProjectInvitationDao invitationDao;
	private final ProjectMembershipDao membershipDao;

	private final Provider<Renderer> renderers;

	private final DevHubMail mail;
	private final String publicUrl;

	private final RegistrationTokenDao tokenDao;

	@Inject
	public ProjectResource(Provider<Renderer> renderers, SecurityProvider securityProvider, ProjectDao projectDao,
			UserDao userDao, ProjectInvitationDao invitationDao, ProjectMembershipDao membershipDao, DevHubMail mail,
			ServerConfig serverConfig, RegistrationTokenDao tokenDao) {
		this.renderers = renderers;

		this.securityProvider = securityProvider;

		this.userDao = userDao;
		this.projectDao = projectDao;
		this.invitationDao = invitationDao;
		this.membershipDao = membershipDao;
		this.mail = mail;
		this.tokenDao = tokenDao;
		this.publicUrl = serverConfig.getWebUrl();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public String serveProjectPage(@PathParam("id") final long id) {
		LOG.trace("Serving page for project: {}", id);

		final User currentUser = securityProvider.getUser();
		final Project project = projectDao.findById(id);

		if (!currentUser.isAdmin()) {
			validateThatUserIsMemberOfProject(currentUser, project);
		}

		final Set<ProjectMembership> members = project.getMembers();
		final List<ProjectInvitation> invitations = invitationDao.findByProject(project);

		return renderers.get()
				.setValue("project", project)
				.setValue("git-path", project.getSourceCodeUrl())
				// .setValue("jenkins-path", jenkinsHost + "job/" +
				// project.getSafeName() + "/")
				.setValue("members", members)
				.setValue("invitations", invitations)
				.setValue("scripts", Lists.newArrayList("invite-user.js"))
				.render("project.tpl");

	}

	@GET
	@Path("{projectId}/invite/{userMail}")
	@Transactional
	public Response inviteUser(@PathParam("projectId") final long projectId, @PathParam("userMail") final String email) {

		LOG.trace("Inviting user {} for project {}", email, projectId);

		if (securityProvider.getUser().getEmail().equals(email)) {
			return Response.status(Status.CONFLICT)
					.entity("You cannot invite yourself")
					.build();
		}
		final Project project = projectDao.findById(projectId);
		if (userIsAlreadyInvited(project, email)) {
			return Response.status(Status.CONFLICT)
					.entity("User is already invited")
					.build();
		}
		ProjectInvitation invitation;
		try {
			invitation = inviteKnownUser(email, project);
		} catch (NoResultException e) {
			invitation = inviteUnkownuser(email, project);
		}
		invitationDao.persist(invitation);
		return Response.ok().build();

	}

	private ProjectInvitation inviteUnkownuser(final String email, final Project project) {
		ProjectInvitation invitation;
		LOG.trace("Invited user is unknown to DevHub, {}", email);
		invitation = new ProjectInvitation(email, project);
		String token = UUID.randomUUID().toString();
		tokenDao.persist(new RegistrationToken(email, token));
		String verifyUrl = publicUrl + "accounts/activate/" + token;
		String myName = securityProvider.getUser().getDisplayName();
		mail.sendDevHubInvite(email, myName, project.getName(), verifyUrl);
		return invitation;
	}

	private ProjectInvitation inviteKnownUser(final String email, final Project project) {
		final User otherUser;
		otherUser = userDao.findByEmail(email);
		LOG.trace("Invited user is known to DevHub, {}", email);
		ProjectInvitation invitation = new ProjectInvitation(otherUser, project);
		String myName = securityProvider.getUser().getDisplayName();
		mail.sendProjectInvite(email, myName, project.getName(), publicUrl);
		return invitation;
	}

	private boolean userIsAlreadyInvited(final Project project, final String email) {
		LOG.trace("Testing whether user {} is already invited for project {} ...", email, project);
		try {
			invitationDao.findByProjectAndEMail(project, email);
			LOG.trace("Invitation found - user is already invited");
			return true;
		} catch (final NoResultException e) {
			LOG.trace("Invitation not found - user is not yet invited");
			return false;
		}
	}

	@GET
	@Path("{id}/invitation")
	@Transactional
	public Response answerInvitation(@PathParam("id") final long id, @QueryParam("accept") final boolean accept) {

		LOG.trace("Answering invitation for project: {} - accept? {}", id, accept);

		final User currentUser = securityProvider.getUser();
		final Project project = projectDao.findById(id);

		final ProjectInvitation invitation = invitationDao.findByProjectAndEMail(project, currentUser.getEmail());

		if (accept) {
			final ProjectMembership membership = new ProjectMembership(currentUser, project);
			membershipDao.persist(membership);
		}

		invitationDao.remove(invitation);

		return Response.ok().build();

	}

	@GET
	@Path("{id}/provision")
	@Transactional
	public Response provisionProject(@PathParam("id") final long id) {

		LOG.trace("Provisioning project: {} - Not yet implemented", id);

		final Project project = projectDao.findById(id);

		final List<ProjectInvitation> invitations = invitationDao.findByProject(project);

		if (!invitations.isEmpty()) {
			return Response.status(Status.CONFLICT).entity("Not all invitations are answered").build();
		}

		// TODO: Provision project.

		return Response.serverError().entity("Not yet implemented. Go to the <a href=\"/dashboard\">dashboard</a>.").build();

	}

	private void validateThatUserIsMemberOfProject(final User user, final Project project) {

		boolean isMember = false;
		for (final ProjectMembership membership : project.getMembers()) {
			if (membership.getUser().getId() == user.getId()) {
				isMember = true;
				break;
			}
		}

		if (!isMember) {
			throw new UnauthorizedException("Current user is not a member of the requested project");
		}

	}

}
