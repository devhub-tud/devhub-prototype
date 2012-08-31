package nl.tudelft.ewi.dea.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.inject.Guice.createInjector;

import javax.persistence.EntityManager;

import nl.tudelft.ewi.dea.di.PersistenceModule;
import nl.tudelft.ewi.dea.liquibase.DatabaseStructure;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.persist.PersistService;

public class DatabaseTest {

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseTest.class);

	private static Injector injector;
	private static DatabaseStructure structure;

	private EntityManager em;
	private PersistService persistService;

	@Before
	public void setUp() {

		injector = createInjector(new PersistenceModule("test-h2", ""));

		structure = injector.getInstance(DatabaseStructure.class);
		structure.create();
		structure.dropAndCreate();

		persistService = injector.getInstance(PersistService.class);
		persistService.start();
		em = injector.getInstance(EntityManager.class);
		beginTransaction();

	}

	@After
	public void tearDown() {

		if (em.getTransaction().isActive()) {
			if (em.getTransaction().getRollbackOnly()) {
				rollbackTransaction();
			} else {
				commitTransaction();
			}
		}
		em.close();

		persistService.stop();

		structure.drop();

	}

	protected final <T> T getInstance(final Class<T> clazz) {
		return injector.getInstance(clazz);
	}

	protected final void beginTransaction() {
		LOG.trace("Beginning transaction ...");
		em.getTransaction().begin();
	}

	protected final void commitTransaction() {
		LOG.trace("Committing transaction ...");
		em.getTransaction().commit();
	}

	protected final void rollbackTransaction() {
		LOG.trace("Rolling back transaction ...");
		em.getTransaction().rollback();
	}

	protected final void markTransactionForRollback() {
		LOG.debug("Marking transaction for rollback ...");
		em.getTransaction().setRollbackOnly();
	}

	protected final void persistAll(final Object... objects) {
		LOG.trace("Persisting object ...");
		for (final Object object : objects) {
			LOG.trace("Persisting object: {}", object);
			checkNotNull(object, "Object should be non-null: " + object);
			em.persist(object);
		}
	}

}