package nl.tudelft.ewi.dea.dao;

import nl.tudelft.ewi.dea.model.Course;

import com.google.inject.ImplementedBy;

@ImplementedBy(CourseDaoImpl.class)
public interface CourseDao extends Dao<Course> {

}
