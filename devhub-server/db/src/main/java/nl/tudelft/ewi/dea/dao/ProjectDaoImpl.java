package nl.tudelft.ewi.dea.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nl.tudelft.ewi.dea.model.Course;
import nl.tudelft.ewi.dea.model.Project;
import nl.tudelft.ewi.dea.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

@Singleton
public class ProjectDaoImpl extends AbstractDaoBase<Project> implements ProjectDao {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectDaoImpl.class);

	@Inject
	public ProjectDaoImpl(final Provider<EntityManager> emProvider) {
		super(emProvider, Project.class);
	}

	@Override
	@Transactional
	public final List<Project> findByUser(final User user) {

		LOG.trace("Find by user: {}", user);

		checkNotNull(user, "user must be non-null");

		final String query = "SELECT p FROM Project p WHERE p.deployed = true AND p.id IN (SELECT pm.project FROM ProjectMembership pm WHERE pm.user.id = :id) ORDER BY p.name ASC";

		final TypedQuery<Project> tq = createQuery(query);
		tq.setParameter("id", user.getId());

		return tq.getResultList();

	}

	@Override
	public final List<Project> findByCourse(final Course course) {

		LOG.trace("Find by course: {}", course);

		checkNotNull(course, "course must be non-null");

		final String query = "SELECT p FROM Project p WHERE p.deployed = true AND p.course.id = :id";

		final TypedQuery<Project> tq = createQuery(query);
		tq.setParameter("id", course.getId());

		return tq.getResultList();

	}

}
