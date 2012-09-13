package nl.tudelft.ewi.dea.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import nl.tudelft.ewi.dea.model.ProjectMembership;

public class ProjectMembershipDaoImpl extends AbstractDaoBase<ProjectMembership> implements ProjectMembershipDao {

	@Inject
	public ProjectMembershipDaoImpl(final EntityManager em) {
		super(em, ProjectMembership.class);
	}

}
