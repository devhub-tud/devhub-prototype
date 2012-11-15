package nl.tudelft.ewi.dea.jaxrs.projects;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
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
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;
import nl.tudelft.ewi.dea.mail.DevHubMail;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.ProjectMembership;
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
	private final String gitHost;

	private final String jenkinsHost;

	@Inject
	public ProjectResource(Provider<Renderer> renderers, SecurityProvider securityProvider, ProjectDao projectDao,
			UserDao userDao, ProjectInvitationDao invitationDao, ProjectMembershipDao membershipDao, DevHubMail mail,
			ServerConfig serverConfig) {
		this.renderers = renderers;

		this.securityProvider = securityProvider;

		this.userDao = userDao;
		this.projectDao = projectDao;
		this.invitationDao = invitationDao;
		this.membershipDao = membershipDao;
		this.mail = mail;
		this.publicUrl = serverConfig.getWebUrl();
		this.gitHost = serverConfig.getGitHost();
		this.jenkinsHost = serverConfig.getJenkinsUrl();
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
				.setValue("git-path", "git@" + gitHost + ":" + project.getSafeName())
				.setValue("jenkins-path", jenkinsHost + "job/" + project.getSafeName() + "/")
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

		final Project project = projectDao.findById(projectId);
		final User otherUser;
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

		final ProjectInvitation invitation = new ProjectInvitation(otherUser, project);
		invitationDao.persist(invitation);

		String fromName = otherUser.getDisplayName();
		if (fromName == null || fromName.isEmpty()) {
			fromName = otherUser.getEmail();
		}
		mail.sendProjectInvite(otherUser.getEmail(), fromName, project.getName(), publicUrl);

		return Response.ok().build();

	}

	private boolean userIsAlreadyInvited(final Project project, final User user) {

		LOG.trace("Testing whether user {} is already invited for project {} ...", user, project);

		boolean userIsAlreadyInvited = true;

		try {
			invitationDao.findByProjectAndUser(project, user);
			LOG.trace("Invitation found - user is already invited");
		} catch (final NoResultException e) {
			LOG.trace("Invitation not found - user is not yet invited");
			userIsAlreadyInvited = false;
		}

		return userIsAlreadyInvited;

	}

	@GET
	@Path("{id}/invitation")
	@Transactional
	public Response answerInvitation(@PathParam("id") final long id, @QueryParam("accept") final boolean accept) {

		LOG.trace("Answering invitation for project: {} - accept? {}", id, accept);

		final User currentUser = securityProvider.getUser();
		final Project project = projectDao.findById(id);

		final ProjectInvitation invitation = invitationDao.findByProjectAndUser(project, currentUser);

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
