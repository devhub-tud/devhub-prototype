package nl.tudelft.ewi.dea.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.UnsentMailAsJson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

public class UnsentMailDaoImpl extends AbstractDaoBase<UnsentMailAsJson> implements UnsentMailDao {

	private static final Logger LOG = LoggerFactory.getLogger(UnsentMailDaoImpl.class);

	@Inject
	UnsentMailDaoImpl(EntityManager em) {
		super(em, UnsentMailAsJson.class);
	}

	@Override
	@Transactional
	public UnsentMailAsJson persist(String emailAsJson) {
		LOG.warn("Persisting unsent message: {}", emailAsJson);
		UnsentMailAsJson mail = new UnsentMailAsJson(emailAsJson);
		super.persist(mail);
		return mail;
	}

	@Override
	@Transactional
	public void remove(long id) {
		LOG.warn("Removing unsent message with id={}", id);
		String query = "DELETE FROM UnsentMailAsJson m WHERE m.id = :id";
		TypedQuery<UnsentMailAsJson> tq = super.createQuery(query);
		tq.setParameter("id", id);
		tq.executeUpdate();
	}

}
