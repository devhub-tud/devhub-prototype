package nl.tudelft.ewi.dea.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import nl.tudelft.ewi.dea.model.Course;

public class CourseDaoImpl extends AbstractDaoBase<Course> implements CourseDao {

	@Inject
	protected CourseDaoImpl(final EntityManager em) {
		super(em, Course.class);
	}

}
