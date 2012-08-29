package nl.tudelft.ewi.dea.di;

import java.nio.file.Paths;

import javax.servlet.ServletContext;

import nl.tudelft.ewi.dea.servlet.OverviewServlet;
import nl.tudelft.ewi.dea.servlet.util.RedirectServlet;
import nl.tudelft.ewi.dea.template.TemplateEngine;

import org.apache.shiro.guice.web.GuiceShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.ServletModule;

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
		install(new PersistenceModule("test-h2"));

		install(new SecurityModule(servletContext));

		bind(TemplateEngine.class).toInstance(new TemplateEngine(Paths.get(servletContext.getRealPath("/templates/"))));

		LOG.debug("Configuring servlets and URLs");
		filter("/*").through(GuiceShiroFilter.class);

		serve("/").with(new RedirectServlet("/overview"));
		serve("/overview").with(OverviewServlet.class);

		// filter("/api/*").through(GuiceContainer.class);
	}
}
