package nl.tudelft.ewi.dea.di;

import org.apache.shiro.guice.web.GuiceShiroFilter;
import org.eclipse.jetty.server.handler.ContextHandler.Context;

import nl.tudelft.ewi.dea.servlet.HelloServlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceServletConfig extends GuiceServletContextListener {

	private final Context servletContext;

	public GuiceServletConfig(Context servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new SecurityModule(servletContext), new JerseyServletModule() {
			@Override
			protected void configureServlets() {
				filter("/*").through(GuiceShiroFilter.class);
				
				bind(HelloServlet.class);

				serve("/*").with(GuiceContainer.class);
			}
		});
	}
}
