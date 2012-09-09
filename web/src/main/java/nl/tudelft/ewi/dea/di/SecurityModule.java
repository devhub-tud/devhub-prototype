package nl.tudelft.ewi.dea.di;

import javax.servlet.ServletContext;

import nl.tudelft.ewi.dea.security.UserValidator;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

/**
 * This is the application's configuration of most security. Some security can
 * also be achieved using annotations so this is not the sole place for
 * configuration.
 */
public class SecurityModule extends AbstractModule {

	public static final int NUMBER_OF_HASH_ITERATIONS = 1024;

	private final ServletContext sc;

	public SecurityModule(final ServletContext sc) {
		this.sc = sc;
	}

	@Override
	public void configure() {
		// This module needs to be installed separately to avoid private bindings.
		install(new ShiroAopModule());

		// Install module containing Shiro configuration.
		install(new ShiroConfigurationModule(sc));
	}

	@Provides
	public HashedCredentialsMatcher matcher() {
		final HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
		hashedCredentialsMatcher.setHashIterations(NUMBER_OF_HASH_ITERATIONS);
		return hashedCredentialsMatcher;
	}

	public static class ShiroConfigurationModule extends ShiroWebModule {

		private static final Logger LOG = LoggerFactory.getLogger(SecurityModule.class);

		public ShiroConfigurationModule(ServletContext sc) {
			super(sc);
		}

		@Override
		@SuppressWarnings("unchecked")
		protected void configureShiroWeb() {
			LOG.debug("Configuring Shiro Security module");

			bindRealm().to(UserValidator.class);
			bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to("/login");

			addFilterChain("/js/**", ANON);
			addFilterChain("/css/**", ANON);
			addFilterChain("/img/**", ANON);
			addFilterChain("/register", ANON);
			addFilterChain("/register/**", ANON);
			addFilterChain("/accounts/activate/*", ANON);
			addFilterChain("/account/*/reset-password/*", ANON);
			addFilterChain("/reset-password", ANON);
			addFilterChain("/reset-password/*", ANON);
			addFilterChain("/logout", LOGOUT);
			addFilterChain("/login", AUTHC);
			addFilterChain("/**", AUTHC);
		}
	}
}