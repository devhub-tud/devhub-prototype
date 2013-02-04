package nl.tudelft.ewi.dea.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.ProjectMembership;
import nl.tudelft.ewi.dea.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.persist.Transactional;

@Singleton
public class ProjectMembershipDaoImpl extends AbstractDaoBase<ProjectMembership> implements ProjectMembershipDao {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectMembershipDaoImpl.class);

	@Inject
	public ProjectMembershipDaoImpl(final Provider<EntityManager> emProvider) {
		super(emProvider, ProjectMembership.class);
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

	@Override
	@Transactional
	public List<User> findByProjectId(long projectId) {
		final String query = "SELECT p FROM ProjectMembership p WHERE p.project.id = :projectId";
		LOG.debug("Running {} with projectId={}", query, projectId);
		final TypedQuery<ProjectMembership> tq = createQuery(query);
		tq.setParameter("projectId", projectId);

		List<ProjectMembership> resultList = tq.getResultList();
		List<User> users = Lists.newArrayList();
		for (ProjectMembership membership : resultList) {
			users.add(membership.getUser());
		}

		return users;
	}

}
