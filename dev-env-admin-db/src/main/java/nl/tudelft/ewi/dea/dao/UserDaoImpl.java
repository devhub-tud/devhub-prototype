package nl.tudelft.ewi.dea.dao;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.List;

import javax.persistence.EntityManager;
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
	@Transactional
	public User findByEmail(final String email) {

		LOG.trace("Find by email: {}", email);

		checkArgument(isNotEmpty(email));

		final String query = "select o from " + entityName + " o where o.email = :email";

		final TypedQuery<User> tq = createQuery(query);
		tq.setParameter("email", email);

		return tq.getSingleResult();

	}

	@Override
	@Transactional
	public List<User> findByEmailSubString(final String email) {

		LOG.trace("Find by email substring: {}", email);

		final String query = "select o from " + entityName + " o where o.email like '%:email%'";

		final TypedQuery<User> tq = createQuery(query);
		tq.setParameter("email", email);

		return tq.getResultList();

	}

}
