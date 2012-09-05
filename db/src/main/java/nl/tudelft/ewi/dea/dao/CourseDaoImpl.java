package nl.tudelft.ewi.dea.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.Course;

import com.google.inject.persist.Transactional;

public class CourseDaoImpl extends AbstractDaoBase<Course> implements CourseDao {

	@Inject
	public CourseDaoImpl(final EntityManager em) {
		super(em, Course.class);
	}

	@Override
	@Transactional
	public final Course findByName(final String name) {

		final String query = "SELECT c FROM Course c WHERE c.name = :name";

		final TypedQuery<Course> tq = createQuery(query);
		tq.setParameter("name", name);

		return tq.getSingleResult();

	}

}
