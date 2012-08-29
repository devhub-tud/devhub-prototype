package nl.tudelft.ewi.dea.di;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import nl.tudelft.jenkins.guice.JenkinsWsClientGuiceModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * This listener is started when the server starts. It should be the ONLY
 * listener so that the startup of the system can be configured from here.
 */
public class ServerStartupListener extends GuiceServletContextListener {

	private static final Logger LOG = LoggerFactory.getLogger(ServerStartupListener.class);
	private Injector injector;
	private ServletContext servletContext;

	@Override
	protected Injector getInjector() {
		try {
			if (injector == null) {
				injector = Guice.createInjector(
						new WebModule(servletContext),
						new ProvisioningModule(),
						new JenkinsWsClientGuiceModule("http://dea.hartveld.com/jenkins"));
			}
			return injector;
		} catch (Exception e) {
			throw createStartupException(e);
		}
	}

	private RuntimeException createStartupException(Exception e) {
		String msg = "FATAL: Unexpected error while starting the application: " + e.getMessage();
		LOG.error(msg, e);
		return new RuntimeException(msg, e);

	}

	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		this.servletContext = servletContextEvent.getServletContext();
		super.contextInitialized(servletContextEvent);
		startApplication();
	}

	private void startApplication() {
		LOG.info("Application is now fully started");
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		LOG.info("Shutting down application");
		LOG.debug("Stopping persist unit");
		injector.getInstance(PersistService.class).stop();
		super.contextDestroyed(servletContextEvent);
		LOG.debug("Clean shutdown completed");
	}
}
