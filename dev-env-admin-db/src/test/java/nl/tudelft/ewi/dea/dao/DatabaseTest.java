package nl.tudelft.ewi.dea.dao;

import static com.google.inject.Guice.createInjector;

import javax.persistence.EntityManager;

import nl.tudelft.ewi.dea.di.PersistenceModule;
import nl.tudelft.ewi.dea.liquibase.DatabaseStructure;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.inject.Injector;
import com.google.inject.persist.PersistService;

public class DatabaseTest {

	private static Injector injector;
	private static DatabaseStructure structure;

	private EntityManager em;
	private PersistService persistService;

	@BeforeClass
	public static void setUpBeforeClass() {}

	@Before
	public void setUp() {

		injector = createInjector(new PersistenceModule("test-h2", ""));

		structure = injector.getInstance(DatabaseStructure.class);
		structure.dropAndCreate();

		persistService = injector.getInstance(PersistService.class);
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
		em.getTransaction().begin();
	}

	protected final void commitTransaction() {
		em.getTransaction().commit();
	}

	protected final void rollbackTransaction() {
		em.getTransaction().rollback();
	}

	protected final void markTransactionForRollback() {
		em.getTransaction().setRollbackOnly();
	}

}