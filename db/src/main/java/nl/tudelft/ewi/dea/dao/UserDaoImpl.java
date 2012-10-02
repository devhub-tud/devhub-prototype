package nl.tudelft.ewi.dea.dao;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.containsNone;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

class UserDaoImpl extends AbstractDaoBase<User> implements UserDao {

	private static final Logger LOG = LoggerFactory.getLogger(UserDaoImpl.class);

	@Inject
	public UserDaoImpl(final EntityManager em) {
		super(em, User.class);
	}

	@Override
	public List<User> findBySubString(final String subString) {

		LOG.trace("Find by substring: {}", subString);

		checkArgument(isNotEmpty(subString), "subString must be non-empty");
		checkArgument(containsNone(subString, '%'), "subString must not contain '%'");

		final String query = "SELECT u FROM User u WHERE u.displayName LIKE :displayName OR u.email LIKE :email OR u.netid LIKE :netid ORDER BY u.displayName";

		final TypedQuery<User> tq = createQuery(query);
		tq.setParameter("displayName", '%' + subString + '%');
		tq.setParameter("email", '%' + subString + '%');
		tq.setParameter("netid", '%' + subString + '%');

		return tq.getResultList();

	}

	@Override
	@Transactional
	public User findByEmail(final String email) throws NoResultException {

		LOG.trace("Find by email: {}", email);

		checkArgument(isNotEmpty(email), "email must be non-empty");

		final String query = "SELECT u FROM User u WHERE u.email = :email";

		final TypedQuery<User> tq = createQuery(query);
		tq.setParameter("email", email);

		return tq.getSingleResult();

	}

	@Override
	@Transactional
	public List<User> findByEmailSubString(final String email) {

		LOG.trace("Find by email substring: {}", email);

		checkArgument(isNotEmpty(email), "email must be non-empty");
		checkArgument(containsNone(email, '%'), "email must not contain '%'");

		final String query = "SELECT u FROM User u WHERE u.email LIKE :email";

		final TypedQuery<User> tq = createQuery(query);
		tq.setParameter("email", '%' + email + '%');

		return tq.getResultList();

	}

}
