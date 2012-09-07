package nl.tudelft.ewi.dea.dao;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.PasswordResetToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

public class PasswordResetTokenDaoImpl extends AbstractDaoBase<PasswordResetToken> implements PasswordResetTokenDao {

	private static final Logger LOG = LoggerFactory.getLogger(PasswordResetTokenDaoImpl.class);

	@Inject
	public PasswordResetTokenDaoImpl(final EntityManager em) {
		super(em, PasswordResetToken.class);
	}

	@Override
	@Transactional
	public final PasswordResetToken findByToken(final String token) {

		LOG.trace("Find by token: {}", token);

		checkArgument(isNotEmpty(token), "token must be non-empty");

		final String query = "SELECT prt FROM PasswordResetToken prt WHERE prt.token = :token";

		final TypedQuery<PasswordResetToken> tq = createQuery(query);
		tq.setParameter("token", token);

		return tq.getSingleResult();

	}

	@Override
	@Transactional
	public PasswordResetToken findByEmail(final String email) {

		LOG.trace("Find by email: {}", email);

		checkArgument(isNotEmpty(email), "email must be non-empty");

		final String query = "SELECT prt FROM PasswordResetToken prt WHERE prt.user.email = :email";

		final TypedQuery<PasswordResetToken> tq = createQuery(query);
		tq.setParameter("email", email);

		return tq.getSingleResult();

	}

}
