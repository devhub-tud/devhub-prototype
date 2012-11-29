package nl.tudelft.ewi.dea.jaxrs.api.projects;

import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import nl.tudelft.ewi.dea.ServerConfig;
import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectInvitationDao;
import nl.tudelft.ewi.dea.dao.RegistrationTokenDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.mail.DevHubMail;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.RegistrationToken;
import nl.tudelft.ewi.dea.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages {@link ProjectInvitation}s.
 * 
 */
public class InviteManager {

	private static final Logger LOG = LoggerFactory.getLogger(InviteManager.class);

	private final ProjectDao projectDao;
	private final ProjectInvitationDao invitationDao;
	private final RegistrationTokenDao tokenDao;
	private final UserDao userDao;
	private final DevHubMail mail;
	private final String publicUrl;

	@Inject
	public InviteManager(ProjectDao projectDao, ProjectInvitationDao invitationDao, RegistrationTokenDao tokenDao, UserDao userDao, DevHubMail mail, ServerConfig config) {
		this.projectDao = projectDao;
		this.invitationDao = invitationDao;
		this.tokenDao = tokenDao;
		this.userDao = userDao;
		this.mail = mail;
		this.publicUrl = config.getWebUrl();
	}

	public void inviteUser(User sessionUser, String email, long projectId) {
		Project project = projectDao.findById(projectId);
		inviteUser(sessionUser, email, project);
	}

	public void inviteUser(User sessionUser, String email, Project project) throws InviteException {
		if (sessionUser.getEmail().equals(email)) {
			throw new InviteException("You cannot invite yourself");
		}

		if (userIsAlreadyInvited(project, email)) {
			throw new InviteException("User is already invited");
		}

		ProjectInvitation invitation;
		try {
			invitation = inviteKnownUser(sessionUser, email, project);
		} catch (NoResultException e) {
			invitation = inviteUnkownuser(sessionUser, email, project);
		}
		invitationDao.persist(invitation);
	}

	private ProjectInvitation inviteUnkownuser(User sessionUser, String email, Project project) {
		ProjectInvitation invitation;
		LOG.trace("Invited user is unknown to DevHub, {}", email);
		invitation = new ProjectInvitation(email, project);
		String token = UUID.randomUUID().toString();
		tokenDao.persist(new RegistrationToken(email, token));
		String verifyUrl = publicUrl + "/account/activate/" + token;
		String myName = sessionUser.getDisplayName();
		mail.sendDevHubInvite(email, myName, project.getName(), verifyUrl);
		return invitation;
	}

	private ProjectInvitation inviteKnownUser(User sessionUser, final String email, final Project project) {
		final User otherUser;
		otherUser = userDao.findByEmail(email);
		LOG.trace("Invited user is known to DevHub, {}", email);
		ProjectInvitation invitation = new ProjectInvitation(otherUser, project);
		String myName = sessionUser.getDisplayName();
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
}
