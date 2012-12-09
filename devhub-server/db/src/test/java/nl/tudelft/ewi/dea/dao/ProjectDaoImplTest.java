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
		final User otherStudent = new User("OtherStudent", "other@example.com", "other", 67890, "salt", "hash", UserRole.USER);

		final Course course = new Course("Some course", owner, null);
		final Project project = new Project("My first project", course);
		final Project otherProject = new Project("Other Project", course);

		project.setDeployed(true);

		final ProjectMembership membership = new ProjectMembership(student, project);
		final ProjectMembership otherMembership = new ProjectMembership(otherStudent, otherProject);

		persistAll(owner, student, otherStudent, course, project, otherProject, membership, otherMembership);

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

	@Test
	public void testThatFindByCourseReturnsResults() throws Exception {

		// Given
		final User owner = new User("Teacher", "teacher@example.com", "teacher", 0, "salt", "hash", UserRole.ADMIN);
		final Course course = new Course("MyFirstCourse", owner, null);
		final Course otherCourse = new Course("OtherCourse", owner, null);

		final Project p0 = new Project("P0", course);
		final Project p1 = new Project("P1", course);
		final Project p2 = new Project("P2", otherCourse);

		p0.setDeployed(true);
		p1.setDeployed(true);
		p2.setDeployed(true);

		persistAll(owner, course, otherCourse, p0, p1, p2);

		// When
		final List<Project> projects = dao.findByCourse(course);

		// Then
		assertThat(projects.size(), is(2));

	}

}
