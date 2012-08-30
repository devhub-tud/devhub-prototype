package nl.tudelft.ewi.dea.security;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import nl.tudelft.ewi.dea.di.SecurityModule;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.junit.Test;

public class TestCredentialsMatcher {

	/**
	 * This test verifies that the AbstractHash and Salt are functioning
	 * correctly.
	 */
	@Test
	public void whenPasswordIsGeneratedTheCredentialsShouldMatch() {
		final CredentialsMatcher matcher = new SecurityModule(null).matcher();
		final String salt = "abc";
		final String plainPassword = "password";
		final String hashedPassword = new UserFactory().hashPassword(plainPassword, salt);
		final AuthenticationInfo info = new SimpleAccount("admin", hashedPassword, SaltTool.getFullSalt(salt), "testrealm");
		final AuthenticationToken token = new UsernamePasswordToken("admin", plainPassword);
		assertThat(matcher.doCredentialsMatch(token, info), is(true));
	}
}
