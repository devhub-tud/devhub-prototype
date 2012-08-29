package nl.tudelft.ewi.dea.security;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import nl.tudelft.ewi.dea.di.SecurityModule;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.crypto.hash.AbstractHash;
import org.junit.Test;

public class TestCredentialsMatcher {

	/**
	 * This test verifies that the {@link AbstractHash} and Salt are functioning
	 * correctly.
	 */
	@Test
	public void whenPasswordIsGeneratedTheCredentialsShouldMatch() {
		CredentialsMatcher matcher = new SecurityModule(null).matcher();
		String salt = "abc";
		String plainPassword = "password";
		String hashedPassword = new UserFactory().hashPassword(plainPassword, salt);
		AuthenticationInfo info = new SimpleAccount("admin", hashedPassword, SaltTool.getFullSalt(salt), "testrealm");
		AuthenticationToken token = new UsernamePasswordToken("admin", plainPassword);
		assertThat(matcher.doCredentialsMatch(token, info), is(true));
	}
}
