package nl.tudelft.ewi.dea.di;

import nl.tudelft.ewi.dea.dao.DatabaseProperties;
import nl.tudelft.ewi.dea.dao.StatisticDao;
import nl.tudelft.ewi.dea.liquibase.DatabaseStructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.google.inject.persist.jpa.JpaPersistModule;

public class PersistenceModule extends AbstractModule {

	private static final Logger LOG = LoggerFactory.getLogger(PersistenceModule.class);
	private final String liquibaseContext;
	private final DatabaseProperties config;

	/**
	 * @param dbName The name of the database as provided in
	 *           META-INF/persistence.xml
	 */
	public PersistenceModule(final DatabaseProperties config, final String context) {
		this.config = config;
		liquibaseContext = context == null ? "" : context;
	}

	@Override
	protected void configure() {
		LOG.debug("Installing JPA Module");
		bind(DatabaseProperties.class).toInstance(config);
		bind(String.class).annotatedWith(Names.named("liquibaseContext")).toInstance(liquibaseContext);

		JpaPersistModule jpaModule = new JpaPersistModule(config.getPersistanceUnit());
		jpaModule.properties(config.asJpaProperties());

		install(jpaModule);
		bind(DatabaseStructure.class).asEagerSingleton();
		bind(StatisticDao.class).asEagerSingleton();
	}
}
