package nl.tudelft.ewi.dea.dao;

import nl.tudelft.ewi.dea.model.ProjectMembership;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectMembershipDaoImpl.class)
public interface ProjectMembershipDao extends Dao<ProjectMembership> {

}
