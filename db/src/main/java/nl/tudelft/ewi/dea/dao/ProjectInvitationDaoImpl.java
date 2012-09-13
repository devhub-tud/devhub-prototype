package nl.tudelft.ewi.dea.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import nl.tudelft.ewi.dea.model.ProjectInvitation;

public class ProjectInvitationDaoImpl extends AbstractDaoBase<ProjectInvitation> implements ProjectInvitationDao {

	@Inject
	public ProjectInvitationDaoImpl(final EntityManager em) {
		super(em, ProjectInvitation.class);
	}

}
