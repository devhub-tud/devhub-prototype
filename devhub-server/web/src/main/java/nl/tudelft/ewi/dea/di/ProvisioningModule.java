package nl.tudelft.ewi.dea.di;

import java.util.Map;
import java.util.Properties;

import nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner.LeaveProjectTask;
import nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner.LeaveProjectTaskFactory;
import nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner.ProvisionTask;
import nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner.ProvisionTaskFactory;
import nl.tudelft.ewi.devhub.services.ServiceModule;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class ProvisioningModule extends AbstractModule {

	private final Map<String, Map<String, Properties>> servicesConfiguration;

	public ProvisioningModule(Map<String, Map<String, Properties>> servicesConfiguration) {
		this.servicesConfiguration = servicesConfiguration;
	}

	@Override
	protected void configure() {
		install(new ServiceModule(servicesConfiguration));

		install(new FactoryModuleBuilder()
				.implement(ProvisionTask.class, ProvisionTask.class)
				.build(ProvisionTaskFactory.class));

		install(new FactoryModuleBuilder()
				.implement(LeaveProjectTask.class, LeaveProjectTask.class)
				.build(LeaveProjectTaskFactory.class));
	}

}
