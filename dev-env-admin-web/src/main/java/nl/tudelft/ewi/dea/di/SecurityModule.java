package nl.tudelft.ewi.dea.di;

import javax.servlet.ServletContext;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.config.Ini;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.realm.text.IniRealm;

import com.google.inject.Provides;

public class SecurityModule extends ShiroWebModule {

	private static final int NUMBER_OF_HASH_ITERATIONS = 1024;

	SecurityModule(ServletContext sc) {
		super(sc);
	}

	protected void configureShiroWeb() {
		try {
			bindRealm().toConstructor(IniRealm.class.getConstructor(Ini.class));
		} catch (NoSuchMethodException e) {
			addError(e);
		}

		addFilterChain("/logout", LOGOUT);
		addFilterChain("/login.jsp", AUTHC);
		addFilterChain("/*.html", AUTHC);
		addFilterChain("/api/*", AUTHC);
		addFilterChain("/", AUTHC);
	}

	@Provides
	public CredentialsMatcher matcher() {
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
		hashedCredentialsMatcher.setHashIterations(NUMBER_OF_HASH_ITERATIONS);
		return hashedCredentialsMatcher;
	}

	@Provides
	Ini loadShiroIni() {
		return Ini.fromResourcePath("classpath:shiro.ini");
	}
}