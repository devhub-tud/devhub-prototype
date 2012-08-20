package nl.tudelft.ewi.dea;

import nl.tudelft.ewi.dea.di.GuiceServletConfig;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.servlet.GuiceFilter;

/**
 * 
 * This class launches the web application in an embedded Jetty container. This
 * is the entry point to the application.
 */
public class ServerStartup {

	public static void main(String[] args) throws Exception {

		// Create the server.
		Server server = new Server(8080);

		ServletContextHandler sch = new ServletContextHandler(server, "/");

		// Add our Guice listener that includes our bindings
		sch.addEventListener(new GuiceServletConfig(sch.getServletContext()));		

		// Then add GuiceFilter and configure the server to
		// reroute all requests through this filter.
		sch.addFilter(GuiceFilter.class, "/*", null);

		// Must add DefaultServlet for embedded Jetty.
		// Failing to do this will cause 404 errors.
		// This is not needed if web.xml is used instead.
		sch.addServlet(DefaultServlet.class, "/");
		
		sch.setResourceBase("web");

		// Start the server
		server.start();
		server.join();
	}

}
