package nl.tudelft.ewi.dea.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.UnsentMail;

import com.google.inject.persist.Transactional;

public class UnsentMailDao {

	private final EntityManager em;

	@Inject
	UnsentMailDao(EntityManager em) {
		this.em = em;
	}

	@Transactional
	public UnsentMail persist(String emailAsJson) {
		UnsentMail mail = new UnsentMail(emailAsJson);
		em.persist(mail);
		return mail;
	}

	@Transactional
	public void remove(long id) {
		String query = "DELETE FROM UnsentMail m WHERE m.id = :id";
		Query tq = em.createQuery(query);
		tq.setParameter("id", id);
		tq.executeUpdate();
	}

	@Transactional
	public List<UnsentMail> getUnsentMails() {
		String query = "SELECT s FROM UnsentMail s";
		TypedQuery<UnsentMail> tq = em.createQuery(query, UnsentMail.class);
		return tq.getResultList();
	}

}
