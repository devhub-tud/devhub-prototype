package nl.tudelft.ewi.dea.di;

import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;
import javax.servlet.ServletContext;

import nl.tudelft.ewi.dea.ServerConfig;
import nl.tudelft.ewi.dea.jaxrs.projects.provisioner.Provisioner;
import nl.tudelft.ewi.dea.mail.MailModule;
import nl.tudelft.ewi.dea.template.TemplateEngine;

import org.apache.shiro.guice.web.GuiceShiroFilter;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.inject.Provides;
import com.google.inject.Scopes;
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

	private final ServerConfig serverConfig;

	public WebModule(final ServletContext servletContext, ServerConfig serverConfig) {
		this.servletContext = servletContext;
		this.serverConfig = serverConfig;
	}

	@Override
	protected void configureServlets() {
		install(new SecurityModule(servletContext));
		install(new PersistenceModule("production-h2", ""));
		filter("/*").through(PersistFilter.class);

		install(new MailModule(serverConfig.getMailConfig()));

		LOG.debug("Configuring servlets and URLs");
		filter("/*").through(GuiceShiroFilter.class);

		bind(Provisioner.class).in(Scopes.SINGLETON);
		bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);

		final Map<String, String> params = Maps.newHashMap();
		params.put("com.sun.jersey.config.property.packages", "nl.tudelft.ewi.dea.jaxrs");
		params.put(ServletContainer.PROPERTY_WEB_PAGE_CONTENT_REGEX, "/.*\\.(html|js|gif|png|css|ico)");
		filter("/*").through(GuiceContainer.class, params);
	}

	@Provides
	@Singleton
	TemplateEngine templateEngine(ExecutorService executor) {
		return new TemplateEngine(Paths.get(servletContext.getRealPath("/templates/")), executor);
	}
}
