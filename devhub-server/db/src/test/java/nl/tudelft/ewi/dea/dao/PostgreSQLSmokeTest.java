package nl.tudelft.ewi.dea.dao;

import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import nl.tudelft.ewi.dea.CommonModule;
import nl.tudelft.ewi.dea.ConfigurationException;
import nl.tudelft.ewi.dea.liquibase.DatabaseStructure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.PersistService;

@RunWith(MockitoJUnitRunner.class)
public class PostgreSQLSmokeTest {

	private static final Logger LOG = LoggerFactory.getLogger(PostgreSQLSmokeTest.class);

	@Mock private PersistService persistService;

	/**
	 * Make sure that you have postgresql installed on localhost, with db
	 * 'devhub-test', user 'devhub-test', password 'TestIt!'.
	 */
	@Test
	public void testThatDatabaseIsCreatedCorrectlyOnPostgreSQL() throws Exception {

		final String postgresqlHost = "dea.hartveld.com";

		final String hostname = InetAddress.getLocalHost().getHostName();
		if (postgresqlHost.equals(hostname)) {
			LOG.debug("Running test...");
		} else {
			LOG.warn("Not running test.");
		}

		assumeThat(hostname, is(postgresqlHost));

		LOG.debug("Creating database structure...");
		InputStream src = DatabaseTest.class.getResourceAsStream("/databaseconfig.test-postgres.json");
		DatabaseProperties props;
		try {
			props = new CommonModule().objectMapper().readValue(src, DatabaseProperties.class);
		} catch (IOException e) {
			throw new ConfigurationException("Could not read test config", e);
		}
		new DatabaseStructure(props, "");

		LOG.debug("Verifying database structure...");
		Persistence.createEntityManagerFactory("test-postgresql", props.asJpaProperties()).close();

		LOG.debug("Dropping database contents...");
		final Properties properties = props.asJpaProperties();
		properties.put("hibernate.hbm2ddl.auto", "create-drop");
		final EntityManagerFactory emf = Persistence.createEntityManagerFactory("test-postgresql", properties);
		final EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery("DROP TABLE public.databasechangelog").executeUpdate();
		em.createNativeQuery("DROP TABLE public.databasechangeloglock").executeUpdate();
		em.getTransaction().commit();
		em.close();
		emf.close();

	}

}
