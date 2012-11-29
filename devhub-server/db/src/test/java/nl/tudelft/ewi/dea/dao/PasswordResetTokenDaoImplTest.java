package nl.tudelft.ewi.dea.dao;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import nl.tudelft.ewi.dea.model.PasswordResetToken;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.junit.Before;
import org.junit.Test;

public class PasswordResetTokenDaoImplTest extends DatabaseTest {

	private PasswordResetTokenDaoImpl dao;

	@Override
	@Before
	public void setUp() {

		super.setUp();

		dao = getInstance(PasswordResetTokenDaoImpl.class);

	}

	@Test
	public void testThatFindByEmailReturnsHit() throws Exception {

		// Given
		final String email0 = "abc";
		final User user0 = new User(email0, email0, email0, 0, email0, email0, UserRole.USER);

		final String token0 = UUID.randomUUID().toString();
		final PasswordResetToken prt0 = new PasswordResetToken(user0, token0);

		final String email1 = "def";
		final User user1 = new User(email1, email1, email1, 0, email1, email1, UserRole.ADMIN);

		final String token1 = UUID.randomUUID().toString();
		final PasswordResetToken prt1 = new PasswordResetToken(user1, token1);

		persistAll(user0, user1, prt0, prt1);

		// When
		final PasswordResetToken retrievedToken = dao.findByEmail(email0);

		// Then
		assertThat(retrievedToken.getUser().getEmail(), is(email0));
		assertThat(retrievedToken.getToken(), is(token0));

	}

	@Test
	public void testThatFindByTokenReturnsHit() throws Exception {

		// Given
		final String email0 = "abc";
		final User user0 = new User(email0, email0, email0, 0, email0, email0, UserRole.USER);

		final String token0 = UUID.randomUUID().toString();
		final PasswordResetToken prt0 = new PasswordResetToken(user0, token0);

		final String email1 = "def";
		final User user1 = new User(email1, email1, email1, 0, email1, email1, UserRole.ADMIN);

		final String token1 = UUID.randomUUID().toString();
		final PasswordResetToken prt1 = new PasswordResetToken(user1, token1);

		persistAll(user0, user1, prt0, prt1);

		// When
		final PasswordResetToken retrievedToken = dao.findByToken(token0);

		// Then
		assertThat(retrievedToken.getUser().getEmail(), is(email0));
		assertThat(retrievedToken.getToken(), is(token0));

	}

}
