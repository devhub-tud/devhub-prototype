package nl.tudelft.ewi.dea.dao;

import static com.google.inject.Guice.createInjector;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import javax.persistence.PersistenceException;

import nl.tudelft.ewi.dea.di.PersistenceModule;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Injector;

public class UserDaoImplTest {

	private static Injector injector;
	private UserDao dao;

	@BeforeClass
	public static void beforeClass() {
		injector = createInjector(new PersistenceModule("test-h2"));
	}

	@Before
	public void before() {
		dao = injector.getInstance(UserDao.class);
		for (User user : dao.list()) {
			dao.delete(user);
		}
	}

	@Test
	public void whenAUserIsSavedItCanAlsoBeFound() {

		User user = newTestUser("harry");

		dao.persist(user);

		User foundUser = dao.findByEmail(user.getMailAddress());

		assertThat(foundUser, is(user));

	}

	private User newTestUser(String ident) {
		User user = new User(ident, ident + "@unittest.com", "abc", "pass", UserRole.USER);
		return user;
	}

	@Test(expected = PersistenceException.class)
	public void whenADuplicateEmailAdressIsCreatedAnErrorIsThrown() {
		User firstUser = newTestUser("harry");
		User secondUser = newTestUser("harry");
		dao.persist(firstUser);
		dao.persist(secondUser);
	}

	@Test(expected = UserNotFoundException.class)
	public void whenAUserIsDeletedItShouldntBeFoundAnymore() {
		User firstUser = newTestUser("deleteUser");
		dao.persist(firstUser);

		dao.delete(firstUser);

		dao.getById(firstUser.getId());
	}
}
