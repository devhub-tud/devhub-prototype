package nl.tudelft.ewi.dea.jaxrs.dashboard;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectInvitationDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

	private static final Logger LOG = LoggerFactory.getLogger(DashboardResource.class);

	private final Provider<Renderer> renderers;

	private final SecurityProvider securityProvider;

	private final UserDao userDao;
	private final ProjectDao projectDao;
	private final ProjectInvitationDao invitationDao;

	@Inject
	public DashboardResource(final Provider<Renderer> renderers, final UserDao userDao, final ProjectDao projectDao, final ProjectInvitationDao invitationDao, final SecurityProvider securityProvider) {
		this.renderers = renderers;

		this.securityProvider = securityProvider;

		this.userDao = userDao;
		this.projectDao = projectDao;
		this.invitationDao = invitationDao;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Transactional
	public String servePage() {

		LOG.debug("Looking up my projects ...");
		final User me = securityProvider.getUser();
		userDao.merge(me);

		final List<Project> projects = projectDao.findByUser(me);
		final List<ProjectInvitation> invitations = invitationDao.findByUser(me);

		LOG.debug("Rendering page ...");

		return renderers.get()
				.setValue("invitations", invitations)
				.setValue("projects", projects)
				.setValue("scripts", Lists.newArrayList("create-new-project.js", "enroll-to-course.js"))
				.render("dashboard.tpl");
	}
}
