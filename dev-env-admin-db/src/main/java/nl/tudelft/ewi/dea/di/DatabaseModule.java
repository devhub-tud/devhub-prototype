package nl.tudelft.ewi.dea.di;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;

public class DatabaseModule extends AbstractModule {

	private final String dbName;

	/**
	 * @param dbName The name of the database as provided in
	 *           META-INF/persistence.xml
	 */
	public DatabaseModule(String dbName) {
		checkArgument(!Strings.isNullOrEmpty(dbName), "DB name must be non-empty");
		this.dbName = dbName;
	}

	@Override
	protected void configure() {
		install(new JpaPersistModule(dbName));
	}

}
