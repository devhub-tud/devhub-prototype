package nl.tudelft.ewi.dea.dao;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectDaoImplTest extends DatabaseTest {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectDaoImplTest.class);

	private ProjectDaoImpl dao;

	@Override
	@Before
	public void setUp() {
		super.setUp();

		dao = getInstance(ProjectDaoImpl.class);
	}

	@Test
	public void testThatFindByUserWorks() {

		// Given
		final User owner = new User("Teacher", "teacher@example.com", "teacher", 0, "salt", "hash", UserRole.ADMIN);
		final User student = new User("Student", "student@example.com", "student", 12345, "salt", "hash", UserRole.USER);

		final Course course = new Course("Some course", owner);
		final Project project = new Project("My first project", course);

		final ProjectMembership membership = new ProjectMembership(student, project);

		persistAll(owner, student, course, project, membership);

		// When
		final List<Project> projects = dao.findByUser(student);

		// Then
		assertThat(projects.size(), is(1));
		final Project retrievedProject = projects.get(0);

		if (retrievedProject == project) {
			LOG.debug("retrievedProject == project");
		}

		assertThat(retrievedProject.getId(), is(project.getId()));
		assertThat(retrievedProject.getName(), is(project.getName()));
		assertThat(retrievedProject.getCourse().getId(), is(project.getCourse().getId()));

	}

}
