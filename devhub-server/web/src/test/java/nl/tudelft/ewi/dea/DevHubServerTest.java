package nl.tudelft.ewi.dea;

import static org.junit.Assert.fail;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevHubServerTest {

	private static final Logger LOG = LoggerFactory.getLogger(DevHubServerTest.class);

	private Server server;

	private int port;

	@Before
	public void setUp() throws Exception {
		server = new Server(0);
		server.setHandler(DevHubServer.buildWebAppContext());
		server.start();
		this.port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	@Test
	public void testThatTheServerCanBeRunAndRootIsGettable() throws Exception {

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://localhost:" + port + "/");

		LOG.info("Executing GET for url: {}", get.getURI());
		HttpResponse response = client.execute(get);

		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			fail("GET response not 200, but: " + response.getStatusLine());
		}

		client.getConnectionManager().shutdown();

	}

}
