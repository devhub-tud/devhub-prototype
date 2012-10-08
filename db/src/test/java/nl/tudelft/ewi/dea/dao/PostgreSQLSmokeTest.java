package nl.tudelft.ewi.dea.dao;

import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeThat;

import java.net.InetAddress;

import javax.persistence.Persistence;

import nl.tudelft.ewi.dea.liquibase.DatabaseStructure;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgreSQLSmokeTest {

	private static final Logger LOG = LoggerFactory.getLogger(PostgreSQLSmokeTest.class);

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
		DatabaseStructure structure = new DatabaseStructure("test-postgresql", "");

		LOG.debug("Verifying database structure...");
		Persistence.createEntityManagerFactory("test-postgresql").close();

		LOG.debug("Dropping database contents...");
		structure.dropStructure();
	}

}
