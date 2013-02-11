package nl.tudelft.ewi.dea.dao;

import java.util.List;

import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectMembershipDaoImpl.class)
public interface ProjectMembershipDao extends Dao<ProjectMembership> {

	boolean hasEnrolled(long courseId, User user);

	List<ProjectMembership> findByProjectId(long projectId);

	ProjectMembership find(long projectId, User user);

	boolean isMemberOf(long projectId, User user);

}
