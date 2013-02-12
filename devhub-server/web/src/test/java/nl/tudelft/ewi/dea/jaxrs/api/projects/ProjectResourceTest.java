package nl.tudelft.ewi.dea.jaxrs.api.projects;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.TestResources;
import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectInvitationDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.dao.RegistrationTokenDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner.Provisioner;
import nl.tudelft.ewi.dea.jaxrs.api.projects.services.ServicesBackend;
import nl.tudelft.ewi.dea.mail.DevHubMail;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProjectResourceTest {

	private static final long PROJECT_ID = 123L;
	private static final String EMAIL = "test@example.com";

	@Mock private SecurityProvider securityProvider;
	@Mock private ProjectDao projectDao;
	@Mock private UserDao userDao;
	@Mock private ProjectInvitationDao invitationDao;
	@Mock private ProjectMembershipDao membershipDao;
	@Mock private ServicesBackend servicesBackend;
	@Mock private DevHubMail mail;
	@Mock private Project project;
	@Mock private User invitedUser;
	@Mock private User sessionUser;
	@Mock private Provisioner provisioner;
	@Mock private RegistrationTokenDao tokenDao;

	private ProjectResource projectResource;
	private InviteManager inviteManager;

	@Before
	public void setup() {
		inviteManager = new InviteManager(projectDao, invitationDao, tokenDao, userDao, mail, TestResources.SERVER_CONFIG);
		projectResource = new ProjectResource(securityProvider, projectDao, invitationDao, membershipDao, servicesBackend, provisioner, inviteManager);
		when(invitedUser.getEmail()).thenReturn(EMAIL);
		when(invitedUser.getDisplayName()).thenReturn("username");
		when(project.getName()).thenReturn("projectname");
		when(project.getId()).thenReturn(PROJECT_ID);
		when(sessionUser.getEmail()).thenReturn("sessionuser@example.com");
		when(sessionUser.getDisplayName()).thenReturn("Display user");

		when(securityProvider.getUser()).thenReturn(sessionUser);
		when(projectDao.findById(PROJECT_ID)).thenReturn(project);
		when(userDao.findByEmail(EMAIL)).thenReturn(invitedUser);
		doThrow(new NoResultException()).when(userDao).findByEmail(not(eq(EMAIL)));
	}

	@Test
	public void whenUserAlreadyInvitedReturnWithError() {
		findInvite();
		Response response = projectResource.inviteUser(PROJECT_ID, EMAIL);
		assertThat(response.getStatus(), is(Status.CONFLICT.getStatusCode()));
	}

	private void findInvite() {
		when(invitationDao.findByProjectAndEMail(project, EMAIL)).thenReturn(null);
	}

	private void dontFindInvite(String email) {
		doThrow(new NoResultException("Test default not found"))
				.when(invitationDao).findByProjectAndEMail(project, email);
	}

	@Test
	public void whenUserInvitedAndTargetUserKnownMailThatUser() {
		dontFindInvite(EMAIL);

		Response response = projectResource.inviteUser(PROJECT_ID, EMAIL);

		ArgumentCaptor<ProjectInvitation> inviteCapt = ArgumentCaptor.forClass(ProjectInvitation.class);
		verify(invitationDao).persist(inviteCapt.capture());
		ProjectInvitation invite = inviteCapt.getValue();
		assertThat(invite.getEmail(), is(EMAIL));
		assertThat(invite.getUser(), is(invitedUser));
		assertThat(invite.getProject(), is(project));
		verify(mail).sendProjectInvite(EMAIL, sessionUser.getDisplayName(), project.getName(), TestResources.SERVER_CONFIG.getWebUrl());
		assertThat(response.getStatus(), is(Status.OK.getStatusCode()));
	}

	@Test
	public void whenUserInvitedAndTargerUserUnknownSendHimDevHubInvite() {
		String unknownUser = "unknown@example.com";
		dontFindInvite(unknownUser);
		Response response = projectResource.inviteUser(PROJECT_ID, unknownUser);

		ArgumentCaptor<ProjectInvitation> inviteCapt = ArgumentCaptor.forClass(ProjectInvitation.class);
		verify(invitationDao).persist(inviteCapt.capture());
		ProjectInvitation invite = inviteCapt.getValue();
		assertThat(invite.getEmail(), is(unknownUser));
		assertThat(invite.getUser(), is(nullValue()));
		assertThat(invite.getProject(), is(project));
		String url = TestResources.SERVER_CONFIG.getWebUrl();
		String myName = sessionUser.getDisplayName();
		String projectName = project.getName();

		verify(mail).sendDevHubInvite(
				eq(unknownUser),
				eq(myName),
				eq(projectName),
				startsWith(url));

		assertThat(response.getStatus(), is(Status.OK.getStatusCode()));
	}

	@Test
	public void whenUserInvitesHimselfReject() {
		Response response = projectResource.inviteUser(PROJECT_ID, sessionUser.getEmail());
		assertThat(response.getStatus(), is(Status.CONFLICT.getStatusCode()));
		assertThat((String) response.getEntity(), is("You cannot invite yourself"));
	}
}
