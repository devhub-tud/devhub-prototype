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

import com.google.inject.Provides;

public class SecurityModule extends ShiroWebModule {

	private static final int NUMBER_OF_HASH_ITERATIONS = 1024;
	public static final ByteSource SALT = new SimpleByteSource("skjhdf9834hj");

	SecurityModule(ServletContext sc) {
		super(sc);
	}

	protected void configureShiroWeb() {
		install(new ShiroAopModule());
		bindRealm().to(IniRealm.class);

		addFilterChain("/logout", LOGOUT);
		addFilterChain("/login.jsp", AUTHC);
		addFilterChain("/*.html", AUTHC);
		addFilterChain("/api/*", AUTHC);
		addFilterChain("/", AUTHC);
	}

	@Provides
	@Singleton
	IniRealm loadIniRealm(Ini ini) {
		System.out.println(" !! INI REALM !! ");
		IniRealm realm = new IniRealm(ini);
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME);
		hashedCredentialsMatcher.setHashIterations(NUMBER_OF_HASH_ITERATIONS);
		realm.setCredentialsMatcher(hashedCredentialsMatcher);

		return realm;
	}

	@Provides
	@Singleton
	Ini loadShiroIni() {
		System.out.println("!!! INI!!!");
		return Ini.fromResourcePath("classpath:shiro.ini");
	}

	public static void main(String[] args) {
		// TODO Add SALT here.
		Sha256Hash hash = new Sha256Hash("test", null, NUMBER_OF_HASH_ITERATIONS);
		System.out.println(hash);
	}
}