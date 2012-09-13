package nl.tudelft.ewi.dea.jaxrs.projects;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectInvitationDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	@Inject
	public ProjectResource(final SecurityProvider securityProvider, final ProjectDao projectDao, final UserDao userDao, final ProjectInvitationDao invitationDao, final ProjectMembershipDao membershipDao) {
		this.securityProvider = securityProvider;

		this.userDao = userDao;
		this.projectDao = projectDao;
		this.invitationDao = invitationDao;
		this.membershipDao = membershipDao;
	}

	@GET
	@Path("{id}")
	public Response serveProjectPage(@PathParam("id") final long id) {

		LOG.trace("Serving page for project: {} - not yet implemented", id);

		// TODO Implement serving project overview page.

		return Response.serverError().entity("Not yet implemented. Go to the <a href=\"/dashboard\">dashboard</a>.").build();

	}

	@GET
	@Path("{projectId}/invite/{userId}")
	public Response inviteUser(@PathParam("projectId") final long projectId, @PathParam("userId") final long userId) {

		LOG.trace("Inviting user {} for project {} - not yet implemented", userId, projectId);

		final Project project = projectDao.findById(projectId);
		final User otherUser = userDao.findById(userId);

		final ProjectInvitation invitation = new ProjectInvitation(otherUser, project);
		invitationDao.persist(invitation);

		// TODO Send email to invited user. Low priority, after demo!

		return Response.serverError().entity("Not yet implemented. Go to the <a href=\"/dashboard\">dashboard</a>.").build();

	}

	@GET
	@Path("{id}/invitation")
	@Produces(MediaType.TEXT_HTML)
	public Response serveInvitationPage(@PathParam("id") final long id) {

		LOG.trace("Serving invitation page for project: {} - Not yet implemented", id);

		// TODO Implement serving of page. Could also be implemented client-side
		// as a pop-up.

		return Response.serverError().entity("Not yet implemented. Go to the <a href=\"/dashboard\">dashboard</a>.").build();

	}

	@POST
	@Path("{id}/invitation")
	public Response answerInvitation(@PathParam("id") final long id, @QueryParam("accept") final boolean accept) {

		LOG.trace("Answering invitation: {} - accept? {} - Not yet implemented", id, accept);

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
	public Response provisionProject(final long id) {

		LOG.trace("Provisioning project: {} - Not yet implemented", id);

		final Project project = projectDao.findById(id);

		final List<ProjectInvitation> invitations = invitationDao.findByProject(project);

		if (!invitations.isEmpty()) {
			return Response.status(Status.CONFLICT).entity("Not all invitations are answered").build();
		}

		// TODO: Provision project.

		return Response.serverError().entity("Not yet implemented. Go to the <a href=\"/dashboard\">dashboard</a>.").build();

	}

}
