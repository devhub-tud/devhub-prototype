package nl.tudelft.ewi.devhub.services;

import java.util.Map;
import java.util.Properties;

import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationModule;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlModule;

import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule {

	private final Map<String, Map<String, Properties>> servicesConfiguration;

	public ServiceModule(Map<String, Map<String, Properties>> servicesConfiguration) {
		this.servicesConfiguration = servicesConfiguration;
	}

	@Override
	protected void configure() {
		install(new VersionControlModule(servicesConfiguration.get("versionControl")));
		install(new ContinuousIntegrationModule(servicesConfiguration.get("continuousIntegration")));
	}

}
