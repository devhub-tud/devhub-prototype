package nl.tudelft.ewi.dea.jaxrs.course;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.inject.Provider;

import nl.tudelft.ewi.dea.dao.CourseDao;
import nl.tudelft.ewi.dea.dao.ProjectDao;
import nl.tudelft.ewi.dea.dao.ProjectMembershipDao;
import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;
import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CourseResourseTest {

	private CourseResource resource;

	@Mock private Provider<Renderer> renderers;
	@Mock private Renderer renderer;

	@Mock private UserDao userDao;
	@Mock private CourseDao courseDao;
	@Mock private ProjectDao projectDao;
	@Mock private ProjectMembershipDao membershipDao;

	@Mock private SecurityProvider securityProvider;

	@Mock private Course course;
	@Mock private User currentUser;

	@Before
	public void setUp() {

		resource = new CourseResource(renderers, userDao, courseDao, projectDao, membershipDao, securityProvider);

	}

	@Test
	public void testThatEnrollWorks() throws Exception {

		final int courseId = 12345;

		when(courseDao.findById(12345)).thenReturn(course);
		when(securityProvider.getUser()).thenReturn(currentUser);

		resource.enroll(courseId);

		verify(currentUser).addProjectMembership(any(Project.class));

		assertThat(true, is(true));

	}

}
