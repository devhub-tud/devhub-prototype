package nl.tudelft.ewi.dea.security;

import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.junit.Before;
import org.junit.Test;

public class UserFactoryTest {

	private UserFactory userFactory;
	private User user;

	@Before
	public void setUp() {
		user = new User("Test user", "test@devhub.nl", "test2", 1234567, "1234", "oldpw", UserRole.USER);
		userFactory = new UserFactory();
	}

	@Test(expected = IllegalArgumentException.class)
	public void userUsingPasswordOfLessThanEightCharacters() {
		userFactory.resetUserPassword(user, "passwor");
	}

	@Test
	public void userUsingPasswordOfEightCharacters() {
		userFactory.resetUserPassword(user, "password");
	}

	@Test
	public void userUsingPasswordOfMoreThanEightCharacters() {
		userFactory.resetUserPassword(user, "password1");
	}

}
