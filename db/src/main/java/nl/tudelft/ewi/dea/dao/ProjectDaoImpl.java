package nl.tudelft.ewi.dea.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

public class ProjectDaoImpl extends AbstractDaoBase<Project> implements ProjectDao {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectDaoImpl.class);

	@Inject
	public ProjectDaoImpl(final EntityManager em) {
		super(em, Project.class);
	}

	@Override
	@Transactional
	public List<Project> findByUser(final User user) {

		LOG.trace("Find by user: {}", user);

		// final String query =
		// "SELECT p FROM Project p WHERE p.members.user.id = :id";
		final String query = "SELECT p FROM Project p WHERE p.id IN (SELECT pm.project FROM ProjectMembership pm WHERE pm.user.id = :id)";

		final TypedQuery<Project> tq = createQuery(query);
		tq.setParameter("id", user.getId());

		return tq.getResultList();

	}

}
