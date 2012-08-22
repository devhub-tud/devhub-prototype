package nl.tudelft.ewi.dea.di;

import nl.tudelft.ewi.dea.servlet.HelloServlet;

import org.apache.shiro.guice.web.GuiceShiroFilter;

import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class WebModule extends ServletModule {

	@Override
	protected void configureServlets() {
		filter("/*").through(GuiceShiroFilter.class);
		
		bind(HelloServlet.class);
		
		filter("/api/*").through(GuiceContainer.class);
	}

}
