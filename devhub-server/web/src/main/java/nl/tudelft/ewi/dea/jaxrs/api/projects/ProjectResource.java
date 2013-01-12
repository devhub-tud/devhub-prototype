package nl.tudelft.ewi.dea.jaxrs.api.projects;

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
import nl.tudelft.ewi.dea.jaxrs.api.projects.services.ServicesBackend;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;

import org.slf4j.Logger;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/project")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResource {

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ProjectResource.class);

	private final SecurityProvider securityProvider;
	private final ProjectDao projectDao;
	private final ProjectInvitationDao invitationDao;
	private final ProjectMembershipDao membershipDao;
	private final InviteManager invateMngr;
	private final ServicesBackend backend;

	@Inject
	public ProjectResource(SecurityProvider securityProvider, ProjectDao projectDao,
			ProjectInvitationDao invitationDao, ProjectMembershipDao membershipDao,
			ServicesBackend backend, InviteManager invateMngr) {

		this.projectDao = projectDao;
		this.invitationDao = invitationDao;
		this.membershipDao = membershipDao;
		this.securityProvider = securityProvider;
		this.invateMngr = invateMngr;
		this.backend = backend;
	}

	@GET
	@Path("{projectId}/invite/{userMail}")
	@Transactional
	public Response inviteUser(@PathParam("projectId") final long projectId, @PathParam("userMail") final String email) {

		LOG.trace("Inviting user {} for project {}", email, projectId);

		try {
			invateMngr.inviteUser(securityProvider.getUser(), email, projectId);
		} catch (InviteException e) {
			LOG.info("Could not invite user", e);
			return Response.status(Status.CONFLICT)
					.entity(e.getMessage())
					.build();
		}

		return Response.ok().build();

	}

	@POST
	@Path("{id}/invitation")
	@Transactional
	public Response answerInvitation(@PathParam("id") final long id, @QueryParam("accept") final boolean accept) {

		LOG.trace("Answering invitation for project: {} - accept? {}", id, accept);

		final User user = securityProvider.getUser();
		final Project project = projectDao.findById(id);

		final ProjectInvitation invitation = invitationDao.findByProjectAndEMail(project, user.getEmail());

		if (accept) {
			ServiceUser serviceUser = new ServiceUser(user.getNetId(), user.getDisplayName(), user.getEmail());
			backend.addMembers(project.getId(), serviceUser);

			final ProjectMembership membership = new ProjectMembership(user, project);
			membershipDao.persist(membership);
		}

		invitationDao.remove(invitation);

		return Response.ok().build();

	}
}
