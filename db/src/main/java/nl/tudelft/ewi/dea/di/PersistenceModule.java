package nl.tudelft.ewi.dea.di;

import static com.google.common.base.Preconditions.checkArgument;
import nl.tudelft.ewi.dea.liquibase.DatabaseStructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.google.inject.persist.jpa.JpaPersistModule;

public class PersistenceModule extends AbstractModule {

	private static final Logger LOG = LoggerFactory.getLogger(PersistenceModule.class);
	private final String dbName;
	private final String liquibaseContext;

	/**
	 * @param dbName The name of the database as provided in
	 *           META-INF/persistence.xml
	 */
	public PersistenceModule(final String dbName, final String context) {
		checkArgument(!Strings.isNullOrEmpty(dbName), "DB name must be non-empty");
		this.dbName = dbName;

		liquibaseContext = context == null ? "" : context;
	}

	@Override
	protected void configure() {
		LOG.debug("Installing JPA Module");

		bind(String.class).annotatedWith(Names.named("persistenceUnit")).toInstance(dbName);
		bind(String.class).annotatedWith(Names.named("liquibaseContext")).toInstance(liquibaseContext);

		install(new JpaPersistModule(dbName));
		bind(DatabaseStructure.class).asEagerSingleton();
	}

}
