package nl.tudelft.ewi.dea.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

import nl.tudelft.ewi.dea.model.PasswordResetToken;

public class PasswordResetTokenDaoImpl extends AbstractDaoBase<PasswordResetToken> implements PasswordResetTokenDao {

	private static final Logger LOG = LoggerFactory.getLogger(PasswordResetTokenDaoImpl.class);

	@Inject
	public PasswordResetTokenDaoImpl(EntityManager em) {
		super(em, PasswordResetToken.class);
	}

	@Override
	@Transactional
	public final PasswordResetToken findByToken(String token) {

		LOG.trace("Find by token: {}", token);

		String query = "SELECT prt FROM PasswordResetToken prt WHERE prt.token = :token";

		TypedQuery<PasswordResetToken> tq = createQuery(query);
		tq.setParameter("token", token);

		return tq.getSingleResult();

	}

}
