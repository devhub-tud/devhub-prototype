package nl.tudelft.ewi.dea.di;

import java.nio.file.Paths;
import java.util.Map;

import javax.servlet.ServletContext;

import nl.tudelft.ewi.dea.jaxrs.home.DashboardResource;
import nl.tudelft.ewi.dea.jaxrs.login.LoginResource;
import nl.tudelft.ewi.dea.jaxrs.projects.ProjectResource;
import nl.tudelft.ewi.dea.template.TemplateEngine;

import org.apache.shiro.guice.web.GuiceShiroFilter;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.inject.Scopes;
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

	public WebModule(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	protected void configureServlets() {
		install(new PersistenceModule("test-h2", ""));

		install(new SecurityModule(servletContext));

		bind(TemplateEngine.class).toInstance(new TemplateEngine(Paths.get(servletContext.getRealPath("/templates/"))));

		LOG.debug("Configuring servlets and URLs");
		filter("/*").through(GuiceShiroFilter.class);

		bind(LoginResource.class);
		bind(ProjectResource.class);
		bind(DashboardResource.class);

		bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);

		Map<String, String> params = Maps.newHashMap();
		params.put(ServletContainer.PROPERTY_WEB_PAGE_CONTENT_REGEX, "/.*\\.(html|js|css)");
		filter("/*").through(GuiceContainer.class, params);
	}
}
