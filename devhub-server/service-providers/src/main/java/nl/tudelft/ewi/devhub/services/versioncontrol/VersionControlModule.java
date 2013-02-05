package nl.tudelft.ewi.devhub.services.versioncontrol;

import java.util.Map;
import java.util.Properties;

import nl.tudelft.ewi.devhub.services.versioncontrol.implementations.GitRepositoryUtils;
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
		GitoliteService gitoliteService = new GitoliteService(configuration.get("Gitolite"),
				new GitRepositoryUtils());

		bind(GitoliteService.class).toInstance(gitoliteService);

		Multibinder.newSetBinder(binder(), VersionControlService.class)
				.addBinding().toInstance(gitoliteService);
	}

}
