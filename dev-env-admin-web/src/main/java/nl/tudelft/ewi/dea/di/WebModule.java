package nl.tudelft.ewi.dea.di;

import nl.tudelft.ewi.dea.servlet.HelloServlet;
import nl.tudelft.ewi.dea.templates.Welcome;

import org.apache.shiro.guice.web.GuiceShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.ServletModule;
import com.google.sitebricks.SitebricksModule;
import com.google.sitebricks.SitebricksServletModule;

/**
 * This module configures the web settings for the application. Some settings
 * are also annotation based so this is not the sole place for URL
 * confiruations.
 */
public class WebModule extends ServletModule {

	private static final Logger LOG = LoggerFactory.getLogger(WebModule.class);

	@Override
	protected void configureServlets() {
		LOG.debug("Configuring servlets and URLs");
		filter("/*").through(GuiceShiroFilter.class);

		install(createSiteBricksModule());

		bind(HelloServlet.class);

		// filter("/api/*").through(GuiceContainer.class);
	}

	private SitebricksModule createSiteBricksModule() {
		return new SitebricksModule() {

			@Override
			protected void configureSitebricks() {
				LOG.debug("Configuring sitebricks");
				scan(Welcome.class.getPackage());
			}

			@Override
			protected SitebricksServletModule servletModule() {
				return new SitebricksServletModule() {
					@Override
					protected void configurePreFilters() {
						filter("/*").through(GuiceShiroFilter.class);
					}
				};
			}

		};
	}

}
