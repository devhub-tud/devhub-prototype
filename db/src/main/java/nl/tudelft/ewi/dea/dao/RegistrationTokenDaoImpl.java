package nl.tudelft.ewi.dea.dao;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.RegistrationToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

public class RegistrationTokenDaoImpl extends AbstractDaoBase<RegistrationToken> implements RegistrationTokenDao {

	private static final Logger LOG = LoggerFactory.getLogger(RegistrationTokenDaoImpl.class);

	@Inject
	public RegistrationTokenDaoImpl(final EntityManager em) {
		super(em, RegistrationToken.class);
	}

	@Override
	@Transactional
	public RegistrationToken findByToken(final String token) {

		LOG.trace("Find by token: {}", token);

		checkArgument(isNotEmpty(token));

		final String query = "SELECT rt FROM RegistrationToken rt WHERE rt.token = :token";

		final TypedQuery<RegistrationToken> tq = createQuery(query);
		tq.setParameter("token", token);

		return tq.getSingleResult();

	}

	@Override
	@Transactional
	public RegistrationToken findByEmail(final String email) {

		LOG.trace("Find by email: {}", email);

		checkArgument(isNotEmpty(email));

		final String query = "SELECT rt FROM RegistrationToken rt WHERE rt.user.email = :email";

		final TypedQuery<RegistrationToken> tq = createQuery(query);
		tq.setParameter("email", email);

		return tq.getSingleResult();

	}

}
