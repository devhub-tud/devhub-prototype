package nl.tudelft.ewi.dea;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevHubServerTest {

	private static final Logger LOG = LoggerFactory.getLogger(DevHubServerTest.class);

	private Server server;

	@Before
	public void setUp() throws Exception {
		server = new Server(8080);
		server.setHandler(DevHubServer.buildWebAppContext());
		server.start();
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	@Test
	public void f() throws Exception {

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://localhost:8080/");

		HttpResponse response = client.execute(get);
		LOG.info("Response status  : {}", response.getStatusLine());
		LOG.info("Response contents: {}", EntityUtils.toString(response.getEntity()));

		client.getConnectionManager().shutdown();

	}

}
