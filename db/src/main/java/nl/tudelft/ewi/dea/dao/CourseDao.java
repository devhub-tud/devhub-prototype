package nl.tudelft.ewi.dea.dao;

import java.util.List;

import nl.tudelft.ewi.dea.model.Course;

import com.google.inject.ImplementedBy;

@ImplementedBy(CourseDaoImpl.class)
public interface CourseDao extends Dao<Course> {

	Course findByName(final String name);

	List<Course> findBySubString(final String subString);

}
