package nl.tudelft.ewi.dea.di;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import nl.tudelft.ewi.dea.mail.MailModule;
import nl.tudelft.ewi.dea.mail.MailProperties;
import nl.tudelft.ewi.dea.template.TemplateEngine;

import org.apache.shiro.guice.web.GuiceShiroFilter;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.google.inject.persist.PersistFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * This module configures the web settings for the application. Some settings
 * are also annotation based so this is not the sole place for URL
 * configurations.
 */
public class WebModule extends ServletModule {

	private static final Logger LOG = LoggerFactory.getLogger(WebModule.class);

	private final ServletContext servletContext;

	public WebModule(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	protected void configureServlets() {
		Properties configuration = loadConfiguration();
		Names.bindProperties(binder(), configuration);

		install(new PersistenceModule("test-h2", ""));
		filter("/*").through(PersistFilter.class);

		install(new SecurityModule(servletContext));
		install(new MailModule(MailProperties.newWithAuth(
				configuration.getProperty("webapp.smtp.host"),
				configuration.getProperty("webapp.smtp.user"),
				configuration.getProperty("webapp.smtp.pass"),
				configuration.getProperty("webapp.smtp.no-reply"),
				"true".equals(configuration.getProperty("webapp.smtp.ssl")),
				Integer.parseInt(configuration.getProperty("webapp.smtp.port")))));

		bind(TemplateEngine.class).toInstance(new TemplateEngine(Paths.get(servletContext.getRealPath("/templates/"))));

		LOG.debug("Configuring servlets and URLs");
		filter("/*").through(GuiceShiroFilter.class);

		bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);

		final Map<String, String> params = Maps.newHashMap();
		params.put("com.sun.jersey.config.property.packages", "nl.tudelft.ewi.dea.jaxrs");
		params.put(ServletContainer.PROPERTY_WEB_PAGE_CONTENT_REGEX, "/.*\\.(html|js|gif|png|css)");
		filter("/*").through(GuiceContainer.class, params);
	}

	private Properties loadConfiguration() {
		try {
			Properties properties = new Properties();
			properties.load(getClass().getResourceAsStream("/config.properties"));
			return properties;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}