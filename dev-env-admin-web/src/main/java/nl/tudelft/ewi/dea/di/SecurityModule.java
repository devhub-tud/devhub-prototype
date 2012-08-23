package nl.tudelft.ewi.dea.di;

import javax.inject.Singleton;
import javax.servlet.ServletContext;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.config.Ini;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provides;

/**
 * This is the application's configuration of most security. Some security can
 * also be achieved using annotations so this is not the sole place for
 * configuration.
 */
public class SecurityModule extends ShiroWebModule {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityModule.class);
	private static final int NUMBER_OF_HASH_ITERATIONS = 1024;
	public static final ByteSource SALT = new SimpleByteSource("skjhdf9834hj");

	SecurityModule(ServletContext sc) {
		super(sc);
	}

	@SuppressWarnings("unchecked")
	protected void configureShiroWeb() {
		LOG.debug("Configuring Shiro Security module");
		install(new ShiroAopModule());

		bindRealm().to(IniRealm.class);

		addFilterChain("/", AUTHC);
		
		addFilterChain("/welcome", AUTHC);
		
		addFilterChain("/logout", LOGOUT);
		addFilterChain("/login.jsp", AUTHC);
		addFilterChain("/*.html", AUTHC);
		addFilterChain("/api/*", AUTHC);

	}

	@Provides
	@Singleton
	IniRealm loadIniRealm(Ini ini) {
		IniRealm realm = new IniRealm(ini);
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
		hashedCredentialsMatcher.setHashIterations(NUMBER_OF_HASH_ITERATIONS);
		realm.setCredentialsMatcher(hashedCredentialsMatcher);
		return realm;
	}

	@Provides
	@Singleton
	Ini loadShiroIni() {
		return Ini.fromResourcePath("classpath:shiro.ini");
	}

	/**
	 * Use this main method to generate passwords.
	 */
	public static void main(String[] args) {
		// TODO Add SALT here.
		Sha256Hash hash = new Sha256Hash("test", null, NUMBER_OF_HASH_ITERATIONS);
		System.out.println(hash);
	}
}