package nl.tudelft.ewi.dea.dao;

import static com.google.inject.Guice.createInjector;
import nl.tudelft.ewi.dea.di.PersistenceModule;
import nl.tudelft.ewi.dea.liquibase.DatabaseStructure;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.google.inject.Injector;

public class DatabaseTest {

	private static Injector injector;
	private static DatabaseStructure structure;

	@BeforeClass
	public static void setUpBeforeClass() {
		injector = createInjector(new PersistenceModule("test-h2", "test"));
		structure = injector.getInstance(DatabaseStructure.class);
	}

	@Before
	public void setUp() {
		structure.dropAndCreate();
	}

	@After
	public void tearDown() {
		structure.drop();
	}

	protected final <T> T getInstance(Class<T> clazz) {
		return injector.getInstance(clazz);
	}

}