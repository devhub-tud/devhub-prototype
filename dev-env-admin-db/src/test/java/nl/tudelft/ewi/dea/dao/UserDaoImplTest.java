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
	}

	@Test
	public void whenAUserIsSavedItCanAlsoBeFound() {

		User user = User.newUserWithRandomSalt("User", "test@abc.com", "pass123");

		dao.persist(user);

		User foundUser = dao.findByEmail("test@abc.com");

		assertThat(foundUser, is(user));

	}

	@Test(expected = PersistenceException.class)
	public void whenADuplicateEmailAdressIsCreatedAnErrorIsThrown() {
		User firstUser = User.newUserWithRandomSalt("First user", "test@abc.com", "pass123");
		User secondUser = User.newUserWithRandomSalt("Second user", "test@abc.com", "pass123");
		dao.persist(firstUser);
		dao.persist(secondUser);
	}

	@Test(expected = UserNotFoundException.class)
	public void whenAUserIsDeletedItShouldntBeFoundAnymore() {
		User firstUser = User.newUserWithRandomSalt("First user", "userToDelete@abc.com", "pass123");
		dao.persist(firstUser);

		dao.delete(firstUser);

		dao.getById(firstUser.getId());
	}
}
