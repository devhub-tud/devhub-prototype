package nl.tudelft.ewi.dea.dao;

import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import javassist.NotFoundException;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;

public class ProjectInvitationDaoImplTest extends DatabaseTest {

	private ProjectInvitationDao invitationDao;

	private final String name = "X";
	private final String otherName = "Y";
	private final String otherMail = "y@example.com";
	private final String mail = "x@example.com";

	private User user;
	private User otherUser;

	private Project project;

	private Course course;

	@Before
	@Override
	public void setUp() {
		super.setUp();
		user = new User(name, mail, name, 0, name, name, UserRole.USER);
		otherUser = new User(otherName, otherMail, otherName, 1, otherName, otherName, UserRole.USER);
		course = new Course("Course", user);
		project = new Project("Other", course);
		invitationDao = getInstance(ProjectInvitationDao.class);
	}

	@Test
	public void testThatFindByProjectReturnsNothingWhenOnlyOtherProjectHasInvite() throws Exception {
		final ProjectInvitation invitation = new ProjectInvitation(user, project);
		final Project otherProject = new Project("Project", course);

		invitationDao.persist(user, course, project, otherProject, invitation);

		// When
		final List<ProjectInvitation> projects = invitationDao.findByProject(otherProject);

		// Then
		assertThat(projects, is(emptyCollectionOf(ProjectInvitation.class)));
	}

	@Test
	public void testThatFindByProjectReturnsAllInvitationsBelongingToTheProject() throws Exception {
		final ProjectInvitation invitation0 = new ProjectInvitation(user, project);
		final Project otherProject = new Project("Project", course);
		final ProjectInvitation invitation1 = new ProjectInvitation(user, otherProject);
		final ProjectInvitation invitation2 = new ProjectInvitation(otherUser, otherProject);

		invitationDao.persist(user, otherUser, course, project, otherProject, invitation0, invitation1, invitation2);

		// When
		final List<ProjectInvitation> projects = invitationDao.findByProject(otherProject);

		// Then
		assertThat(projects, hasSize(2));

	}

	@Test(expected = PersistenceException.class)
	public void whenAUserIsInvitedMultipleTimesForAProjectAViolationIsThrown() throws Exception {
		final User otherOwner = new User(otherName, otherMail, otherName, 1, otherName, otherName, UserRole.USER);
		final ProjectInvitation invitation0 = new ProjectInvitation(user, project);

		final Project otherProject = new Project("Project", course);
		final ProjectInvitation invitation1 = new ProjectInvitation(user, otherProject);
		final ProjectInvitation invitation2 = new ProjectInvitation(user, otherProject);

		invitationDao.persist(user, otherOwner, course, project, otherProject, invitation0, invitation1, invitation2);
	}

	@Test
	public void testThatFindByProjectAndUserReturnsNoInvitationWhenNotInvited() throws Exception {
		final ProjectInvitation invitation = new ProjectInvitation(user, project);

		invitationDao.persist(user, otherUser, course, project, invitation);

		// When
		boolean exceptionWasThrown = false;
		try {
			invitationDao.findByProjectAndEMail(project, otherUser.getEmail());
		} catch (final NoResultException e) {
			exceptionWasThrown = true;
		}

		// Then
		assertThat(exceptionWasThrown, is(true));

	}

	@Test
	public void testThatFindByProjectAndMailReturnsInvitationWhenInvited() throws Exception {
		final ProjectInvitation invitation0 = new ProjectInvitation(otherUser, project);

		final ProjectInvitation invitation1 = new ProjectInvitation(user, project);

		invitationDao.persist(otherUser, user, course, project, invitation0, invitation1);

		// When
		final ProjectInvitation retrievedInvitation = invitationDao.findByProjectAndEMail(project, user.getEmail());

		// Then
		assertThat(retrievedInvitation, is(notNullValue()));
		assertThat(retrievedInvitation.getUser(), is(user));
		assertThat(retrievedInvitation.getProject(), is(project));

	}

	@Test
	public void testThatFindByUserReturnsNoInvitationWhenNotInvited() throws Exception {
		final ProjectInvitation invitation0 = new ProjectInvitation(otherUser, project);

		invitationDao.persist(otherUser, user, course, project, invitation0);

		// When
		final List<ProjectInvitation> invitations = invitationDao.findByUser(user);

		// Then
		assertThat(invitations.isEmpty(), is(true));
	}

	@Test
	public void testThatFindByUserReturnsInvitationWhenInvited() throws Exception {
		final ProjectInvitation invitation0 = new ProjectInvitation(otherUser, project);
		final ProjectInvitation invitation1 = new ProjectInvitation(user, project);

		invitationDao.persist(otherUser, user, course, project, invitation0, invitation1);

		// When
		final List<ProjectInvitation> invitations = invitationDao.findByUser(user);

		// Then
		assertThat(invitations, hasSize(1));
	}

	@Test
	public void testPersistingInviteWithNullUserIsAllowed() {
		ProjectInvitation invite = new ProjectInvitation(otherMail, project);
		invitationDao.persist(user, course, project, invite);
		ProjectInvitation inviteFound = invitationDao.findByProjectAndEMail(project, otherMail);
		assertThat(inviteFound.getUser(), is(nullValue()));
	}

	@Test(expected = PersistenceException.class)
	public void duplicateInvitesbasedOnMailNotAllowed() {
		ProjectInvitation invite = new ProjectInvitation(otherMail, project);
		ProjectInvitation invite2 = new ProjectInvitation(otherMail, project);
		invitationDao.persist(user, course, project, invite, invite2);
	}

	@Test
	public void whenNewUserAddedUpdateTableIsDoneCorrectly() {
		final ProjectInvitation invitation = new ProjectInvitation(mail, project);
		invitationDao.persist(otherUser, user, course, project, invitation);
		invitationDao.updateInvitesForNewUser(user);
		assertThat(invitationDao.findByUser(user).size(), is(1));
	}
}
