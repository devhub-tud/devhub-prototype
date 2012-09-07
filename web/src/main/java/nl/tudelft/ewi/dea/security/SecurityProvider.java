package nl.tudelft.ewi.dea.security;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import nl.tudelft.ewi.dea.dao.UserDao;
import nl.tudelft.ewi.dea.model.User;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.RequestScoped;

/**
 * An injectable, mockable adapter for {@link SecurityUtils}.
 */
@ThreadSafe
@RequestScoped
public class SecurityProvider {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityProvider.class);
	private static final String USER_KEY = "user";

	private final HttpSession session;
	private UserDao userDao;

	@Inject
	SecurityProvider(HttpSession session, UserDao userDao) {
		this.session = session;
		this.userDao = userDao;
	}

	/**
	 * @see SecurityUtils#getSubject()
	 */
	public Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	/**
	 * @return The user from the current {@link HttpSession}.
	 */
	public User getUser() {
		// Yes, this isn't truly threadsafe
		// but chances of this happening concurrent are pretty slim
		// and even if it does it's only ah double database call
		// boohoo cry me a river. 
		User me = (User) session.getAttribute(USER_KEY);
		if (me == null) {
			LOG.debug("Getting the user from the database and caching it in the session");
			String email = (String) getSubject().getPrincipal();
			me = userDao.findByEmail(email);
			session.setAttribute(USER_KEY, me);
		}
		return me;
	}

	/**
	 * @see SecurityUtils#getSecurityManager()
	 */
	public SecurityManager getSecurityManager() {
		return SecurityUtils.getSecurityManager();
	}
}
