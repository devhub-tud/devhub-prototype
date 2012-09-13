package nl.tudelft.ewi.dea.dao;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

public abstract class AbstractDaoBase<T> implements Dao<T> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractDaoBase.class);

	private final EntityManager em;

	private final Class<T> entityClass;
	protected final String entityName;

	protected AbstractDaoBase(final EntityManager em, final Class<T> clazz) {

		this.em = em;

		entityClass = clazz;
		entityName = entityClass.getSimpleName();

	}

	@Override
	@Transactional
	public final List<T> findAll() {

		LOG.trace("{}: Find all...", entityName);

		final TypedQuery<T> query = createQuery("FROM " + entityName);

		return query.getResultList();

	}

	@Override
	@Transactional
	public final T findById(final long id) {

		LOG.trace("{}: Find by id: {}", entityName, id);

		final T result = em.find(entityClass, id);

		if (result == null) {
			throw new NoResultException("No result found for id: " + id);
		}

		return result;

	}

	@Override
	@Transactional
	public final void persist(final T object) {

		LOG.trace("{}: Persist: {}", entityName, object);

		checkNotNull(object, "object must be non-null");

		em.persist(object);

	}

	@Override
	@Transactional
	public final void persist(final Object... objects) {

		LOG.trace("{}: Persist: {}", entityName, objects);

		checkNotNull(objects, "objects must be non-null");

		for (final Object object : objects) {
			em.persist(object);
		}

	}

	@Override
	@Transactional
	public void detach(final T entity) {

		checkNotNull(entity, "entity must be non-null");

		em.detach(entity);

	}

	@Override
	@Transactional
	public final T merge(final T entity) {

		checkNotNull(entity, "entity must be non-null");

		return em.merge(entity);

	}

	@Override
	@Transactional
	public final void remove(final T object) {

		LOG.trace("{}: Remove: {}", entityName, object);

		checkNotNull(object, "object must be non-null");

		em.remove(object);

	}

	@Override
	@Transactional
	public final void remove(final Object... objects) {

		LOG.trace("{}: Remove: {}", entityName, objects);

		checkNotNull(objects, "objects must be non-null");

		for (final Object object : objects) {
			em.remove(object);
		}
	}

	@Transactional
	protected final CriteriaBuilder getCriteriaBuilder() {
		return em.getCriteriaBuilder();
	}

	@Transactional
	protected final TypedQuery<T> createQuery(final String query) {

		checkArgument(isNotEmpty(query), "query must be non-empty");

		return em.createQuery(query, entityClass);

	}

}
