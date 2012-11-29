package nl.tudelft.ewi.dea.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.UnsentMailAsJson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

public class UnsentMailDao {

	private static final Logger LOG = LoggerFactory.getLogger(UnsentMailDao.class);
	private final EntityManager em;

	@Inject
	UnsentMailDao(EntityManager em) {
		this.em = em;
	}

	@Transactional
	public UnsentMailAsJson persist(String emailAsJson) {
		LOG.warn("Persisting an unsent message");
		UnsentMailAsJson mail = new UnsentMailAsJson(emailAsJson);
		em.persist(mail);
		return mail;
	}

	@Transactional
	public void remove(long id) {
		LOG.warn("Removing unsent message with id={}", id);
		String query = "DELETE FROM UnsentMailAsJson m WHERE m.id = :id";
		Query tq = em.createQuery(query);
		tq.setParameter("id", id);
		tq.executeUpdate();
	}

	@Transactional
	public List<UnsentMailAsJson> getUnsentMails() {
		String query = "SELECT s FROM UnsentMailAsJson s";
		TypedQuery<UnsentMailAsJson> tq = em.createQuery(query, UnsentMailAsJson.class);
		return tq.getResultList();
	}

}
