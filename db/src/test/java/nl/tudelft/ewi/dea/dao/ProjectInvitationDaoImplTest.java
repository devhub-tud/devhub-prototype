package nl.tudelft.ewi.dea.dao;

import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.persistence.NoResultException;

import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.junit.Before;
import org.junit.Test;

public class ProjectInvitationDaoImplTest extends DatabaseTest {

	private ProjectInvitationDao invitationDao;

	@Override
	@Before
	public void setUp() {
		super.setUp();

		invitationDao = getInstance(ProjectInvitationDao.class);
	}

	@Test
	public void testThatFindByProjectReturnsNothingWhenOnlyOtherProjectHasInvite() throws Exception {

		// Given

		final String name = "X";
		final User owner = new User(name, name, name, 0, name, name, UserRole.USER);
		final Course course = new Course("Course", owner);
		final Project other = new Project("Other", course);
		final ProjectInvitation invitation = new ProjectInvitation(owner, other);

		final Project project = new Project("Project", course);

		invitationDao.persist(owner, course, other, project, invitation);

		// When
		final List<ProjectInvitation> projects = invitationDao.findByProject(project);

		// Then
		assertThat(projects, is(emptyCollectionOf(ProjectInvitation.class)));

	}

	@Test
	public void testThatFindByProjectReturnsAllInvitationsBelongingToTheProject() throws Exception {

		// Given

		final String name = "X";
		final User owner = new User(name, name, name, 0, name, name, UserRole.USER);
		final Course course = new Course("Course", owner);
		final Project other = new Project("Other", course);
		final ProjectInvitation invitation0 = new ProjectInvitation(owner, other);

		final Project project = new Project("Project", course);
		final ProjectInvitation invitation1 = new ProjectInvitation(owner, project);
		final ProjectInvitation invitation2 = new ProjectInvitation(owner, project);

		invitationDao.persist(owner, course, other, project, invitation0, invitation1, invitation2);

		// When
		final List<ProjectInvitation> projects = invitationDao.findByProject(project);

		// Then
		assertThat(projects, hasSize(2));

	}

	@Test
	public void testThatFindByProjectAndUserReturnsNoInvitationWhenNotInvited() throws Exception {

		// Given

		final String name = "X";
		final User owner = new User(name, name, name, 0, name, name, UserRole.USER);
		final Course course = new Course("Course", owner);
		final Project project = new Project("Project", course);
		final ProjectInvitation invitation = new ProjectInvitation(owner, project);

		final String u = "U";
		final User user = new User(u, u, u, 0, u, u, UserRole.USER);

		invitationDao.persist(owner, user, course, project, invitation);

		// When
		boolean exceptionWasThrown = false;
		try {
			invitationDao.findByProjectAndUser(project, user);
		} catch (final NoResultException e) {
			exceptionWasThrown = true;
		}

		// Then
		assertThat(exceptionWasThrown, is(true));

	}

	@Test
	public void testThatFindByProjectAndUserReturnsInvitationWhenInvited() throws Exception {

		// Given

		final String name = "X";
		final User owner = new User(name, name, name, 0, name, name, UserRole.USER);
		final Course course = new Course("Course", owner);
		final Project project = new Project("Project", course);
		final ProjectInvitation invitation0 = new ProjectInvitation(owner, project);

		final String u = "U";
		final User user = new User(u, u, u, 0, u, u, UserRole.USER);
		final ProjectInvitation invitation1 = new ProjectInvitation(user, project);

		invitationDao.persist(owner, user, course, project, invitation0, invitation1);

		// When
		final ProjectInvitation retrievedInvitation = invitationDao.findByProjectAndUser(project, user);

		// Then
		assertThat(retrievedInvitation, is(notNullValue()));
		assertThat(retrievedInvitation.getUser(), is(user));
		assertThat(retrievedInvitation.getProject(), is(project));

	}

	@Test
	public void testThatFindByUserReturnsNoInvitationWhenNotInvited() throws Exception {

		// Given

		final String name = "X";
		final User other = new User(name, name, name, 0, name, name, UserRole.USER);
		final Course course = new Course("Course", other);
		final Project project = new Project("Project", course);
		final ProjectInvitation invitation0 = new ProjectInvitation(other, project);

		final String u = "U";
		final User user = new User(u, u, u, 0, u, u, UserRole.USER);

		invitationDao.persist(other, user, course, project, invitation0);

		// When
		final List<ProjectInvitation> invitations = invitationDao.findByUser(user);

		// Then
		assertThat(invitations.isEmpty(), is(true));

	}

	@Test
	public void testThatFindByUserReturnsInvitationWhenInvited() throws Exception {

		// Given

		final String name = "X";
		final User other = new User(name, name, name, 0, name, name, UserRole.USER);
		final Course course = new Course("Course", other);
		final Project project = new Project("Project", course);
		final ProjectInvitation invitation0 = new ProjectInvitation(other, project);

		final String u = "U";
		final User user = new User(u, u, u, 0, u, u, UserRole.USER);
		final ProjectInvitation invitation1 = new ProjectInvitation(user, project);

		invitationDao.persist(other, user, course, project, invitation0, invitation1);

		// When
		final List<ProjectInvitation> invitations = invitationDao.findByUser(user);

		// Then
		assertThat(invitations, hasSize(1));

	}

}
