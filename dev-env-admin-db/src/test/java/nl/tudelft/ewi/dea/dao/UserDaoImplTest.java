package nl.tudelft.ewi.dea.dao;

import static com.google.inject.Guice.createInjector;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import javax.persistence.PersistenceException;

import nl.tudelft.ewi.dea.PersistenceStartStopHandler;
import nl.tudelft.ewi.dea.di.DatabaseModule;
import nl.tudelft.ewi.dea.model.User;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Injector;

public class UserDaoImplTest {

	private static Injector injector;
	private UserDao dao;

	@BeforeClass
	public static void beforeClass() {
		injector = createInjector(new DatabaseModule("test-h2"));
		injector.getInstance(PersistenceStartStopHandler.class).start();
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

		User user = new User(0, "User", "test@abc.com");

		dao.persist(user);

		User foundUser = dao.findByEmail("test@abc.com");

		assertThat(foundUser, is(user));

	}

	@Test(expected = PersistenceException.class)
	public void whenADuplicateEmailAdressIsCreatedAnErrorIsThrown() {
		User firstUser = new User(0, "First user", "test@abc.com");
		User secondUser = new User(0, "Second user", "test@abc.com");
		dao.persist(firstUser);
		dao.persist(secondUser);
	}

	@Test(expected = UserNotFoundException.class)
	public void whenAUserIsDeletedItShouldntBeFoundAnymore() {
		User firstUser = new User(0, "First user", "userToDelete@abc.com");
		dao.persist(firstUser);

		dao.delete(firstUser);

		dao.getById(firstUser.getId());
	}
}
