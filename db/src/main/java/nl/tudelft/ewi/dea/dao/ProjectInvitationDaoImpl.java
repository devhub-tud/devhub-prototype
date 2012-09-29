package nl.tudelft.ewi.dea.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

public class ProjectInvitationDaoImpl extends AbstractDaoBase<ProjectInvitation> implements ProjectInvitationDao {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectInvitationDaoImpl.class);

	@Inject
	public ProjectInvitationDaoImpl(final EntityManager em) {
		super(em, ProjectInvitation.class);
	}

	@Override
	@Transactional
	public ProjectInvitation findByProjectAndUser(final Project project, final User user) {

		LOG.trace("Find by project {} and user {}", project, user);

		checkNotNull(project, "project must be non-null");
		checkNotNull(user, "user must be non-null");

		final String query = "SELECT pi FROM ProjectInvitation pi WHERE pi.project = :project AND pi.user = :user";

		final TypedQuery<ProjectInvitation> tq = createQuery(query);
		tq.setParameter("project", project);
		tq.setParameter("user", user);

		return tq.getSingleResult();

	}

	@Override
	@Transactional
	public List<ProjectInvitation> findByProject(final Project project) {

		LOG.trace("Find by project: {}", project);

		checkNotNull(project, "project must be non-null");

		final String query = "SELECT pi FROM ProjectInvitation pi WHERE pi.project = :project";

		final TypedQuery<ProjectInvitation> tq = createQuery(query);
		tq.setParameter("project", project);

		return tq.getResultList();

	}

	@Override
	public List<ProjectInvitation> findByUser(final User user) {

		LOG.trace("Find by user: {}", user);

		checkNotNull(user, "user must be non-null");

		final String query = "SELECT pi FROM ProjectInvitation pi WHERE pi.user = :user";

		final TypedQuery<ProjectInvitation> tq = createQuery(query);
		tq.setParameter("user", user);

		return tq.getResultList();

	};

}
