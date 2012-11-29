package nl.tudelft.ewi.dea.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.SshKey;
import nl.tudelft.ewi.dea.model.User;

import com.google.common.base.Preconditions;
import com.google.inject.persist.Transactional;

class SshKeyDaoImpl extends AbstractDaoBase<SshKey> implements SshKeyDao {

	@Inject
	public SshKeyDaoImpl(EntityManager em) {
		super(em, SshKey.class);
	}

	@Override
	@Transactional
	public List<SshKey> list(User user) {
		Preconditions.checkNotNull(user);
		String query = "SELECT s FROM SshKey s WHERE s.user = :user";
		TypedQuery<SshKey> tq = createQuery(query);
		tq.setParameter("user", user);

		return tq.getResultList();
	}

}
