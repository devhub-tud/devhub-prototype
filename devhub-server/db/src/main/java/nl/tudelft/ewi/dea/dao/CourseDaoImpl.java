package nl.tudelft.ewi.dea.dao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.Course;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

@Singleton
public class CourseDaoImpl extends AbstractDaoBase<Course> implements CourseDao {

	private static final Logger LOG = LoggerFactory.getLogger(CourseDaoImpl.class);

	@Inject
	public CourseDaoImpl(final Provider<EntityManager> emProvider) {
		super(emProvider, Course.class);
	}

	@Override
	@Transactional
	public final Course findByName(final String name) {

		LOG.trace("Find by name: {}", name);

		final String query = "SELECT c FROM Course c WHERE c.name = :name";

		final TypedQuery<Course> tq = createQuery(query);
		tq.setParameter("name", name);

		return tq.getSingleResult();

	}

	@Override
	@Transactional
	public final List<Course> find(final Boolean enrolled, final String subString) {
		LOG.trace("Find by subString: {}", subString);

		// TODO: Make sure we can also filter out enrolled / non-enrolled...

		if (subString == null || subString.isEmpty()) {
			String query = "SELECT c FROM Course c ORDER BY c.name";
			TypedQuery<Course> tq = createQuery(query);
			return tq.getResultList();
		}
		else {
			String query = "SELECT c FROM Course c WHERE c.name like :name ORDER BY c.name";
			TypedQuery<Course> tq = createQuery(query);
			tq.setParameter("name", '%' + subString + '%');
			return tq.getResultList();
		}
	}
}
