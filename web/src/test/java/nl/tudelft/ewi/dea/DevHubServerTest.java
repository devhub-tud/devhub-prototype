package nl.tudelft.ewi.dea;

import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevHubServerTest {

	private static final Logger LOG = LoggerFactory.getLogger(DevHubServerTest.class);

	private static final int PORT = 10_000;

	private Server server;

	@Before
	public void setUp() throws Exception {
		Assert.assertThat("Port is available", available(PORT), is(true));
		server = new Server(PORT);
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
		HttpGet get = new HttpGet("http://localhost:" + PORT + "/");

		HttpResponse response = client.execute(get);
		LOG.info("Response status  : {}", response.getStatusLine());
		LOG.info("Response contents: {}", EntityUtils.toString(response.getEntity()));

		client.getConnectionManager().shutdown();

	}

	/**
	 * Checks to see if a specific port is available.
	 * 
	 * @param port the port to check for availability
	 */
	public static boolean available(int port) {
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}
		return false;
	}

}
