package nl.tudelft.ewi.dea.di;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import nl.tudelft.ewi.dea.DevHubException;
import nl.tudelft.ewi.dea.ServerConfig;
import nl.tudelft.ewi.dea.template.TemplateEngine;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Guice;
import com.google.inject.Injector;
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
		ServerConfig config = readServerConfig();
		LOG.info("Starting with configuration: " + config);
		try {
			if (injector == null) {
				injector = Guice.createInjector(
						new WebModule(servletContext, config),
						new ProvisioningModule(config.getServices()));
			}
			return injector;
		} catch (Exception e) {
			throw createStartupException(e);
		}
	}

	@VisibleForTesting
	public ServerConfig readServerConfig() {
		ObjectMapper mapper = new ObjectMapper();
		InputStream configAsJson = ServerStartupListener.class.getResourceAsStream("/serverconfig.json");
		checkNotNull(configAsJson, "Config file not found!");
		try {
			ServerConfig config = mapper.readValue(configAsJson, ServerConfig.class);
			config.verifyConfig();
			return config;
		} catch (IOException e) {
			throw createStartupException(new IOException("Could not read the server config file " + e.getMessage(), e));
		}
	}

	private RuntimeException createStartupException(Exception e) {
		String msg = "FATAL: Unexpected error while starting the application: " + e.getMessage();
		LOG.error(msg, e);
		return new DevHubException(msg, e);

	}

	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		this.servletContext = servletContextEvent.getServletContext();
		super.contextInitialized(servletContextEvent);
		startApplication();
	}

	private void startApplication() {
		LOG.info("Application is now fully started");
		injector.getInstance(TemplateEngine.class).watchForChanges();
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		super.contextDestroyed(servletContextEvent);
		LOG.info("Stopping application");
		ExecutorService executor = injector.getInstance(ExecutorService.class);
		executor.shutdownNow();
		try {
			LOG.debug("Waiting for executor to shut down");
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOG.info("Thread stopped while waiting for executor to stop. Things might be lost now...");
		}
		LOG.info("DevHub is shut down");
	}

}
