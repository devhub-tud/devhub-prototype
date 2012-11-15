package nl.tudelft.ewi.devhub.services;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;

import com.google.common.collect.Maps;

@Singleton
public class ServiceProvider {

	private final Map<String, VersionControlService> versioningServices;
	private final Map<String, ContinuousIntegrationService> buildServices;

	@Inject
	public ServiceProvider(Set<VersionControlService> versioningServices, Set<ContinuousIntegrationService> buildServices) {
		this.versioningServices = map(versioningServices);
		this.buildServices = map(buildServices);
	}

	private <T extends Service> Map<String, T> map(Set<T> services) {
		Map<String, T> mapped = Maps.newTreeMap();
		for (T service : services) {
			String name = service.getName();
			if (name == null) {
				throw new NullPointerException("Service: " + service.getClass().getSimpleName() + " must return a unique non-null name!");
			}
			if (mapped.containsKey(name)) {
				throw new IllegalArgumentException("There's already a service registered with the name: " + name);
			}
			mapped.put(name, service);
		}
		return mapped;
	}

	public Set<String> getVersionControlServices() {
		return versioningServices.keySet();
	}

	public VersionControlService getVersionControlService(String name) {
		return versioningServices.get(name);
	}

	public Set<String> getContinuousIntegrationServices() {
		return buildServices.keySet();
	}

	public ContinuousIntegrationService getContinuousIntegrationService(String name) {
		return buildServices.get(name);
	}

}
