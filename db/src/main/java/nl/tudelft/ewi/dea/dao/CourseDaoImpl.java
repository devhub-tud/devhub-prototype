package nl.tudelft.ewi.dea.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.Course;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

public class CourseDaoImpl extends AbstractDaoBase<Course> implements CourseDao {

	private static final Logger LOG = LoggerFactory.getLogger(CourseDaoImpl.class);

	@Inject
	public CourseDaoImpl(final EntityManager em) {
		super(em, Course.class);
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
	public final List<Course> findBySubString(final String subString) {

		LOG.trace("Find by subString: {}", subString);

		final String query = "SELECT c FROM Course c WHERE c.name like :name";

		final TypedQuery<Course> tq = createQuery(query);
		tq.setParameter("name", '%' + subString + '%');

		return tq.getResultList();

	}

}
