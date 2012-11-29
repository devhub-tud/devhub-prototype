package nl.tudelft.ewi.devhub.services.continuousintegration;

import java.util.Map;
import java.util.Properties;

import nl.tudelft.ewi.devhub.services.continuousintegration.implementations.JenkinsService;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class ContinuousIntegrationModule extends AbstractModule {

	private final Map<String, Properties> configuration;

	public ContinuousIntegrationModule(Map<String, Properties> configuration) {
		this.configuration = configuration;
	}

	@Override
	protected void configure() {
		Multibinder.newSetBinder(binder(), ContinuousIntegrationService.class)
				.addBinding().toInstance(new JenkinsService(configuration.get("Jenkins")));
	}
}
