package nl.tudelft.ewi.dea.di;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.google.inject.persist.jpa.JpaPersistModule;

public class DatabaseModule extends AbstractModule {

	private final String dbName;
	private final String liquibaseContext;

	/**
	 * @param dbName The name of the database as provided in
	 *           META-INF/persistence.xml
	 */
	public DatabaseModule(String dbName) {
		this(dbName, null);
	}

	/**
	 * @param dbName The name of the database as provided in
	 *           META-INF/persistence.xml
	 * 
	 * @param context The context to use when upgrading the database structure.
	 *           Default is NULL.
	 */
	public DatabaseModule(String dbName, String context) {
		checkArgument(!Strings.isNullOrEmpty(dbName), "DB name must be non-empty");

		this.dbName = dbName;
		this.liquibaseContext = (context == null) ? "" : context;
	}

	@Override
	protected void configure() {
		bind(String.class).annotatedWith(Names.named("persistenceUnit")).toInstance(dbName);
		bind(String.class).annotatedWith(Names.named("liquibaseContext")).toInstance(liquibaseContext);
		install(new JpaPersistModule(dbName));
	}
}
