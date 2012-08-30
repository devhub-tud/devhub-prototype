package nl.tudelft.ewi.dea.di;

import javax.servlet.ServletContext;

import nl.tudelft.ewi.dea.security.UserValidator;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provides;
import com.google.inject.name.Names;

/**
 * This is the application's configuration of most security. Some security can
 * also be achieved using annotations so this is not the sole place for
 * configuration.
 */
public class SecurityModule extends ShiroWebModule {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityModule.class);
	public static final int NUMBER_OF_HASH_ITERATIONS = 1024;

	public SecurityModule(ServletContext sc) {
		super(sc);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void configureShiroWeb() {
		LOG.debug("Configuring Shiro Security module");
		install(new ShiroAopModule());

		bindRealm().to(UserValidator.class);
		bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to("/login");

		addFilterChain("/js/**", ANON);
		addFilterChain("/css/**", ANON);
		addFilterChain("/register", ANON);
		addFilterChain("/account/activate", ANON);
		addFilterChain("/logout", LOGOUT);
		addFilterChain("/login", AUTHC);
		addFilterChain("/**", AUTHC);
	}

	@Provides
	public HashedCredentialsMatcher matcher() {
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
		hashedCredentialsMatcher.setHashIterations(NUMBER_OF_HASH_ITERATIONS);
		return hashedCredentialsMatcher;
	}

}