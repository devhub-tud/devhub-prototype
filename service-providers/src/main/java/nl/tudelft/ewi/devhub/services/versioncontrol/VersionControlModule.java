package nl.tudelft.ewi.devhub.services.versioncontrol;

import java.util.Map;
import java.util.Properties;

import nl.tudelft.ewi.devhub.services.versioncontrol.implementations.GitoliteService;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class VersionControlModule extends AbstractModule {

	private final Map<String, Properties> configuration;

	public VersionControlModule(Map<String, Properties> configuration) {
		this.configuration = configuration;
	}

	@Override
	protected void configure() {
		Multibinder.newSetBinder(binder(), VersionControlService.class)
				.addBinding().toInstance(new GitoliteService(configuration.get("Gitolite")));
	}
}
