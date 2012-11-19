package nl.tudelft.ewi.dea.jaxrs.html;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectInvitationDao;
import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import org.apache.shiro.authz.UnauthorizedException;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("project")
public class ProjectPage {

	private final ProjectDao projectDao;
	private final ProjectInvitationDao invitationDao;
	private final SecurityProvider securityProvider;
	private final Renderer renderer;

	@Inject
	public ProjectPage(Renderer renderer, SecurityProvider securityProvider,
			ProjectDao projectDao, ProjectInvitationDao invitationDao) {

		this.securityProvider = securityProvider;
		this.projectDao = projectDao;
		this.invitationDao = invitationDao;
		this.renderer = renderer;
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public String serveProjectPage(@PathParam("id") long id) {
		User currentUser = securityProvider.getUser();
		Project project = projectDao.findById(id);

		if (!currentUser.isAdmin()) {
			validateThatUserIsMemberOfProject(currentUser, project);
		}

		Set<ProjectMembership> members = project.getMembers();
		List<ProjectInvitation> invitations = invitationDao.findByProject(project);

		return renderer
				.setValue("project", project)
				.setValue("git-path", project.getSourceCodeUrl())
				// .setValue("jenkins-path", jenkinsHost + "job/" +
				// project.getSafeName() + "/")
				.setValue("members", members)
				.setValue("invitations", invitations)
				.addJS("invite-user.js")
				.render("project.tpl");
	}

	private void validateThatUserIsMemberOfProject(User user, Project project) {
		boolean isMember = false;
		for (ProjectMembership membership : project.getMembers()) {
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
