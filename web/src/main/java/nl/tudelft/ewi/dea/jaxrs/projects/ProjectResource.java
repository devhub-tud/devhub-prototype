package nl.tudelft.ewi.dea.jaxrs.projects;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("project")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResource {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectResource.class);

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

		// TODO Implement inviting other users.

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

		// TODO: Accept or reject invitation.

		return Response.serverError().entity("Not yet implemented. Go to the <a href=\"/dashboard\">dashboard</a>.").build();

	}

	@GET
	@Path("{id}/provision")
	public Response provisionProject(final long id) {

		LOG.trace("Provisioning project: {} - Not yet implemented", id);

		// TODO: Check that all invitations are answered (accepted/rejected).
		// TODO: Provision project.

		return Response.serverError().entity("Not yet implemented. Go to the <a href=\"/dashboard\">dashboard</a>.").build();

	}

}
