package nl.tudelft.ewi.dea.di;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import nl.tudelft.ewi.dea.CommonModule;
import nl.tudelft.ewi.dea.ConfigurationException;
import nl.tudelft.ewi.dea.ServerConfig;
import nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner.Provisioner;
import nl.tudelft.ewi.dea.mail.internals.MailSender;
import nl.tudelft.ewi.dea.template.TemplateEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * This listener is started when the server starts. It should be the ONLY
 * listener so that the startup of the system can be configured from here.
 */
public class ServerStartupListener extends GuiceServletContextListener {

	@VisibleForTesting
	public static ServerConfig readServerConfig(ObjectMapper mapper) {
		InputStream configAsJson = ServerStartupListener.class.getResourceAsStream("/serverconfig.json");
		checkNotNull(configAsJson, "Config file not found!");
		try {
			ServerConfig config = mapper.readValue(configAsJson, ServerConfig.class);
			config.verifyConfig();
			return config;
		} catch (IOException e) {
			throw new ConfigurationException("Could not read the server config file " + e.getMessage(), e);
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(ServerStartupListener.class);

	private Injector injector;
	private ServletContext servletContext;
	private boolean inErrorMode = false;

	@Override
	protected Injector getInjector() {
		CommonModule commonModule = new CommonModule();
		ServerConfig config = readServerConfig(commonModule.objectMapper());
		LOG.info("Starting with configuration: " + config);
		try {
			if (injector == null) {
				injector = Guice.createInjector(commonModule,
						new WebModule(servletContext, config),
						new ProvisioningModule(config.getServices()));
			} else {
				throw new IllegalStateException("Injector was already created?");
			}
			return injector;
		} catch (RuntimeException e) {
			inErrorMode = true;
			injector = switchToErrorMode(new Exception(e));
			return injector;
		}
	}

	private Injector switchToErrorMode(Exception e) {
		String msg = "FATAL: Unexpected error while starting the application: " + e.getMessage();
		LOG.error(msg, e);
		return Guice.createInjector(new ServerUnavailableWebModule());
	}

	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		this.servletContext = servletContextEvent.getServletContext();
		super.contextInitialized(servletContextEvent);
		startApplication();
	}

	private void startApplication() {
		if (!inErrorMode) {
			injector.getInstance(TemplateEngine.class).watchForChanges();
			injector.getInstance(MailSender.class).initialize();
		}
		LOG.info("Application is now fully started");
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		super.contextDestroyed(servletContextEvent);

		LOG.info("Stopping application");
		Provisioner executor = injector.getInstance(Provisioner.class);
		MailSender mailer = injector.getInstance(MailSender.class);

		try {
			LOG.info("Shutting down provisioner...");
			executor.shutdown(10000);
		} catch (InterruptedException e) {
			LOG.warn("Failed to shutdown Provisioner in 10 seconds...", e);
		}

		LOG.info("Shutting down mailer...");
		mailer.shutdown();

		LOG.info("DevHub is shut down");
	}

}
