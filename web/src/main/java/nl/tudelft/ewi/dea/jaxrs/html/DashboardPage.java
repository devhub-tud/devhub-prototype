package nl.tudelft.ewi.dea.jaxrs.html;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectInvitationDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("dashboard")
@Produces(MediaType.TEXT_HTML)
public class DashboardPage {

	private final UserDao userDao;
	private final ProjectDao projectDao;
	private final ProjectInvitationDao invitationDao;
	private final SecurityProvider securityProvider;
	private final Renderer renderer;

	@Inject
	public DashboardPage(Renderer renderer, UserDao userDao, ProjectDao projectDao,
			ProjectInvitationDao invitationDao, SecurityProvider securityProvider) {

		this.userDao = userDao;
		this.projectDao = projectDao;
		this.invitationDao = invitationDao;
		this.securityProvider = securityProvider;
		this.renderer = renderer;
	}

	@GET
	@Transactional
	public String servePage() {
		User me = securityProvider.getUser();
		userDao.merge(me);

		List<Project> projects = projectDao.findByUser(me);
		List<ProjectInvitation> invitations = invitationDao.findByUser(me);

		return renderer
				.setValue("invitations", invitations)
				.setValue("projects", projects)
				.addJS("common.js")
				.addJS("dashboard.js")
				.addJS("invites.js")
				.render("dashboard.tpl");
	}
}
