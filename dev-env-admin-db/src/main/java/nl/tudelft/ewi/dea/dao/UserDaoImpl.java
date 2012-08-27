package nl.tudelft.ewi.dea.dao;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.User_;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

class UserDaoImpl implements UserDao {

	private final EntityManager em;

	@Inject
	UserDaoImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional
	public User getById(long id) {
		User user = em.find(User.class, id);
		if (user == null) {
			throw new UserNotFoundException(id);
		} else {
			return user;
		}
	}

	@Override
	@Transactional
	public void persist(User t) {
		em.persist(t);
	}

	@Override
	public User findByEmail(String emailAddres) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> userRoot = criteria.from(User.class);
		criteria.select(userRoot).where(builder.equal(userRoot.get(User_.mailAddress), emailAddres));
		return em.createQuery(criteria).getSingleResult();
	}

	@Override
	public void delete(User firstUser) {
		em.remove(firstUser);
	}

}