package nl.tudelft.ewi.dea.dao;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDaoImplTest extends DatabaseTest {

	private static final Logger LOG = LoggerFactory.getLogger(UserDaoImplTest.class);

	private UserDao dao;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		dao = getInstance(UserDao.class);
	}

	@Test
	public void whenAUserIsSavedItCanAlsoBeFound() {
		final User user = newTestUser("harry");
		dao.persist(user);
		final User foundUser = dao.findByEmail(user.getMailAddress());
		assertThat(foundUser, is(user));
	}

	@Test
	public void whenADuplicateEmailAddressIsCreatedAnErrorIsThrown() {

		final User firstUser = newTestUser("harry");
		final User secondUser = newTestUser("harry");
		dao.persist(firstUser);

		boolean exceptionWasThrown = false;
		try {
			dao.persist(secondUser);
		} catch (final PersistenceException e) {
			LOG.trace("Intentional exception caught: {}", e.getMessage(), e);
			exceptionWasThrown = true;
			markTransactionForRollback();
		}

		assertThat(exceptionWasThrown, is(true));

	}

	@Test
	public void whenAUserIsDeletedItShouldntBeFoundAnymore() {

		// Given
		final User firstUser = newTestUser("deleteUser");
		dao.persist(firstUser);

		// When
		dao.remove(firstUser);

		// Then
		boolean exceptionWasThrown = false;
		try {
			dao.findById(firstUser.getId());
		} catch (final NoResultException e) {
			LOG.trace("Expected exception caught: {}", e.getMessage(), e);
			exceptionWasThrown = true;
			markTransactionForRollback();
		}
		assertThat(exceptionWasThrown, is(true));

	}

	private User newTestUser(final String ident) {
		final User user = new User(ident, ident + "@example.com", "abc", "pass", UserRole.USER);
		return user;
	}

}
