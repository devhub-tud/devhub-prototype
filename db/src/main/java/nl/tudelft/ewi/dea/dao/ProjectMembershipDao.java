package nl.tudelft.ewi.dea.dao;

import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectMembershipDaoImpl.class)
public interface ProjectMembershipDao extends Dao<ProjectMembership> {

	boolean hasEnrolled(Course course, User user);

}
