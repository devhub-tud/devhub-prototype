package nl.tudelft.ewi.dea.dao;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import javax.persistence.PersistenceException;

import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.junit.Before;
import org.junit.Test;

public class UserDaoImplTest extends DatabaseTest {

	UserDao dao;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		dao = getInstance(UserDao.class);
	}

	@Test
	public void whenAUserIsSavedItCanAlsoBeFound() {
		User user = newTestUser("harry");
		dao.persist(user);
		User foundUser = dao.findByEmail(user.getMailAddress());
		assertThat(foundUser, is(user));
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
		dao.findById(firstUser.getId());
	}

	private User newTestUser(String ident) {
		User user = new User(ident, ident + "@unittest.com", "abc", "pass", UserRole.USER);
		return user;
	}

}
