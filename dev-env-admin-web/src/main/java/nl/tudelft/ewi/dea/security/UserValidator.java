package nl.tudelft.ewi.dea.security;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Set;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.dao.UserNotFoundException;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class validates the {@link User}. It is used by Shiro to validate users.
 * In Shiro terms, this class is a {@link Realm}.
 * 
 */
public class UserValidator extends AuthorizingRealm {

	private static final Logger LOG = LoggerFactory.getLogger(UserValidator.class);
	private final UserDao userDao;

	@Inject
	UserValidator(UserDao userDao, HashedCredentialsMatcher matcher) {
		super(matcher);
		this.userDao = userDao;
		this.setName(UserValidator.class.getSimpleName());
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		LOG.debug("Checking doGetAuthorizationInfo for {}", principals);
		// null usernames are invalid
		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(UserRole.ALL_ROLES);

		String email = (String) getAvailablePrincipal(principals);
		User user = userDao.findByEmail(email);

		Set<String> userRoles = UserRole.getRolesFor(user);
		info.setStringPermissions(userRoles);
		LOG.debug("User {} was assigned permissions: {}", email, userRoles);
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
			throws AuthenticationException {
		String email = extractMail(token);
		LOG.debug("Looking for user {}", email);

		try {
			User user = userDao.findByEmail(email);
			LOG.debug("Found user {}", user);
			char[] password = user.getPassword().toCharArray();
			ByteSource salt = SaltTool.getFullSalt(user);
			return new SimpleAuthenticationInfo(email, password, salt, getName());
		} catch (UserNotFoundException e) {
			LOG.debug("User not found: {}", email);
			throw new UnknownAccountException("No account found for user [" + email + "]");
		}
	}

	private String extractMail(AuthenticationToken token) {
		LOG.info("Checking doGetAuthenticationInfo");
		checkArgument(token instanceof UsernamePasswordToken, "Expected a usernamePassword token");
		UsernamePasswordToken usernamePassword = (UsernamePasswordToken) token;
		String email = usernamePassword.getUsername();
		if (email == null) {
			throw new AccountException("Null usernames are not allowed by this realm.");
		}
		return email;
	}

}
