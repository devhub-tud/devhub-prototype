package nl.tudelft.ewi.dea.dao;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.junit.Before;
import org.junit.Test;

public class CourseDaoImplTest extends DatabaseTest {

	private CourseDaoImpl dao;

	@Override
	@Before
	public void setUp() {

		super.setUp();

		dao = getInstance(CourseDaoImpl.class);

	}

	@Test
	public void testThatFindByNameReturnsHits() throws Exception {

		// Given
		final String salt = "abc";

		final String courseName0 = "AAA BBB CCC";
		final String user0 = "f-b-n-1@example.com";
		final User owner0 = new User(user0, user0, user0, 0, salt, user0, UserRole.ADMIN);
		final Course c0 = new Course(courseName0, owner0, null);

		final String courseName1 = "AAA BBB DDD";
		final String user1 = "f-b-n-2@example.com";
		final User owner1 = new User(user1, user1, user1, 0, salt, user1, UserRole.ADMIN);
		final Course c1 = new Course(courseName1, owner1, null);

		persistAll(owner0, owner1, c0, c1);

		// When
		final Course course = dao.findByName(courseName0);

		// Then
		assertThat(course.getName(), is(courseName0));
		assertThat(course.getOwner().getDisplayName(), is(user0));

	}

	@Test
	public void testThatFindBySubStringReturnsHits() throws Exception {

		// Given
		final String salt = "abc";

		final String courseName0 = "AAA BBB CCC";
		final String user0 = "f-b-ss-1@example.com";
		final User owner0 = new User(user0, user0, user0, 0, salt, user0, UserRole.ADMIN);
		final Course c0 = new Course(courseName0, owner0, null);

		final String courseName1 = "AAA BBB DDD";
		final String user1 = "f-b-ss-2@example.com";
		final User owner1 = new User(user1, user1, user1, 0, salt, user1, UserRole.ADMIN);
		final Course c1 = new Course(courseName1, owner1, null);

		final String courseName2 = "AAA EEE";
		final String user2 = "f-b-ss-3@example.com";
		final User owner2 = new User(user2, user2, user2, 0, salt, user2, UserRole.ADMIN);
		final Course c2 = new Course(courseName2, owner2, null);

		persistAll(owner0, owner1, owner2, c0, c1, c2);

		// When
		final List<Course> courses = dao.find(null, "BB");

		// Then
		assertThat(courses.size(), is(2));

	}

	@Test
	public void testFindAll() {
		dao.findAll();
	}

}
