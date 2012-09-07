package nl.tudelft.ewi.dea.dao;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import nl.tudelft.ewi.dea.model.RegistrationToken;

import org.junit.Before;
import org.junit.Test;

public class RegistrationTokenDaoImplTest extends DatabaseTest {

	private RegistrationTokenDao dao;

	@Override
	@Before
	public void setUp() {

		super.setUp();

		dao = getInstance(RegistrationTokenDao.class);

	}

	@Test
	public void testThatFindByEmailReturnsHit() throws Exception {

		// Given
		final String email = "x@y.z";
		final String token = UUID.randomUUID().toString();

		final RegistrationToken regToken = new RegistrationToken(email, token);

		dao.persist(regToken);

		// When
		final RegistrationToken storedToken = dao.findByEmail(email);

		// Then
		assertThat(storedToken.getToken(), is(token));

	}

	@Test
	public void testThatFindByTokenReturnsHit() throws Exception {

		// Given
		final String email0 = "ABC";
		final String token0 = UUID.randomUUID().toString();
		final RegistrationToken regToken0 = new RegistrationToken(email0, token0);

		final String email1 = "DEF";
		final String token1 = UUID.randomUUID().toString();
		final RegistrationToken regToken1 = new RegistrationToken(email1, token1);

		dao.persist(regToken0, regToken1);

		// When

		final RegistrationToken retrievedToken = dao.findByEmail(email0);

		// Then
		assertThat(retrievedToken.getEmail(), is(email0));
		assertThat(retrievedToken.getToken(), is(token0));

	}

}
