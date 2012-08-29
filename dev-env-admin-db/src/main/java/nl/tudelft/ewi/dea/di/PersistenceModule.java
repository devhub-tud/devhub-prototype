package nl.tudelft.ewi.dea.di;

import static com.google.common.base.Preconditions.checkArgument;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

public class PersistenceModule extends AbstractModule {

	private static final Logger LOG = LoggerFactory.getLogger(PersistenceModule.class);
	private final String dbName;

	/**
	 * @param dbName The name of the database as provided in
	 *           META-INF/persistence.xml
	 */
	public PersistenceModule(String dbName) {
		checkArgument(!Strings.isNullOrEmpty(dbName), "DB name must be non-empty");
		this.dbName = dbName;
	}

	@Override
	protected void configure() {
		LOG.debug("Installing JPA Module");
		install(new JpaPersistModule(dbName));
		bind(PersistenceStarter.class).asEagerSingleton();
	}

	private static class PersistenceStarter {

		private final Logger log = LoggerFactory.getLogger(PersistenceModule.PersistenceStarter.class);

		@Inject
		PersistenceStarter(PersistService service) {
			log.info("Starting persistence!");
			service.start();
			log.debug("Persistence started");
		}
	}

}
