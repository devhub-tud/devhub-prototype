package nl.tudelft.ewi.dea.security;

import static org.mockito.Mockito.when;

import javax.inject.Provider;

import nl.tudelft.ewi.dea.dao.UserDao;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserValidatorTest {

	@Mock private Provider<UserDao> userDaoProvider;

	private UserValidator userValidator;

	@Before
	public void setup() {
		final HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
		userValidator = new UserValidator(userDaoProvider, matcher);
	}

	@Test(expected = UnknownAccountException.class)
	public void whenAUserCanotBefoundTheCorrectShiroExceptionIsThrown() {
		final String mailAdress = "test@test.com";
		when(userDaoProvider.get().findByEmail(mailAdress)).thenReturn(null);
		final AuthenticationToken token = new UsernamePasswordToken(mailAdress, "password");
		userValidator.doGetAuthenticationInfo(token);
	}

}