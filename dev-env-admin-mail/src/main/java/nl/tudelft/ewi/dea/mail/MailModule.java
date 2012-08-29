package nl.tudelft.ewi.dea.mail;

import javax.inject.Singleton;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class MailModule extends AbstractModule {

	@Override
	protected void configure() {
		// TODO Auto-generated method stub

	}

	@Provides
	@Singleton
	VelocityEngine velocityEngine() {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

		engine.init();
		return engine;
	}

}
