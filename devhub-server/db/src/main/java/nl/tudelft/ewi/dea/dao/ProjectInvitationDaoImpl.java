package nl.tudelft.ewi.dea.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.ProjectInvitation;
import nl.tudelft.ewi.dea.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

@Singleton
public class ProjectInvitationDaoImpl extends AbstractDaoBase<ProjectInvitation> implements ProjectInvitationDao {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectInvitationDaoImpl.class);

	@Inject
	public ProjectInvitationDaoImpl(final Provider<EntityManager> emProvider) {
		super(emProvider, ProjectInvitation.class);
	}

	@Override
	@Transactional
	public ProjectInvitation findByProjectAndEMail(final Project project, final String userEmail)
			throws NoResultException {

		LOG.trace("Find by project {} and user {}", project, userEmail);

		checkNotNull(project, "project must be non-null");
		checkNotNull(userEmail, "user must be non-null");

		String query = "SELECT pi FROM ProjectInvitation pi WHERE pi.project = :project AND pi.email = :userEmail";

		TypedQuery<ProjectInvitation> tq = createQuery(query);
		tq.setParameter("project", project);
		tq.setParameter("userEmail", userEmail);

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

	}

	@Override
	public void updateInvitesForNewUser(User user) {
		LOG.trace("Finding invites for new user {}", user);
		final String query = "SELECT pi FROM ProjectInvitation pi WHERE pi.email = :email";

		final TypedQuery<ProjectInvitation> tq = createQuery(query);
		tq.setParameter("email", user.getEmail());

		for (ProjectInvitation invite : tq.getResultList()) {
			invite.setUser(user);
			persist(user);
			LOG.trace("Update invite for user {} to project {}", user, invite.getProject());
		}

	};

}
