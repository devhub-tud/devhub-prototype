package nl.tudelft.ewi.dea.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

public class ProjectMembershipDaoImpl extends AbstractDaoBase<ProjectMembership> implements ProjectMembershipDao {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectMembershipDaoImpl.class);

	@Inject
	public ProjectMembershipDaoImpl(final EntityManager em) {
		super(em, ProjectMembership.class);
	}

	@Override
	@Transactional
	public boolean hasEnrolled(final long courseId, final User user) {
		LOG.trace("Check if has enrolled: {} - {}", courseId, user);

		checkNotNull(user, "course must be non-null");

		final String query = "SELECT p FROM ProjectMembership p WHERE p.project.course.id = :courseId AND p.user.id = :userId";

		final TypedQuery<ProjectMembership> tq = createQuery(query);
		tq.setParameter("courseId", courseId);
		tq.setParameter("userId", user.getId());

		return !tq.getResultList().isEmpty();
	}

}
