package nl.tudelft.ewi.dea.di;

import java.util.Map;
import java.util.Properties;

import nl.tudelft.ewi.devhub.services.ServiceModule;

import com.google.inject.AbstractModule;

public class ProvisioningModule extends AbstractModule {

	private final Map<String, Map<String, Properties>> servicesConfiguration;

	public ProvisioningModule(Map<String, Map<String, Properties>> servicesConfiguration) {
		this.servicesConfiguration = servicesConfiguration;
	}

	@Override
	protected void configure() {
		install(new ServiceModule(servicesConfiguration));
	}

}
