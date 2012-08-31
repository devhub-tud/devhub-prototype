package nl.tudelft.ewi.dea.dao;

import javax.persistence.EntityManager;

import nl.tudelft.ewi.dea.model.Project;

public class ProjectDaoImpl extends AbstractDaoBase<Project> implements ProjectDao {

	protected ProjectDaoImpl(final EntityManager em) {
		super(em, Project.class);
	}

}
