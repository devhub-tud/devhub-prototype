package nl.tudelft.ewi.dea.di;

import java.io.IOException;

import nl.minicom.gitolite.manager.ConfigManager;
import nl.minicom.gitolite.manager.git.PassphraseCredentialsProvider;
import nl.minicom.gitolite.manager.models.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

public class ProvisioningModule extends AbstractModule {

	private static final Logger LOG = LoggerFactory.getLogger(ProvisioningModule.class);

	@Override
	protected void configure() {

		final ConfigManager manager = ConfigManager.create("git@dea.hartveld.com:gitolite-admin", new PassphraseCredentialsProvider(""));

		try {
			final Config config = manager.getConfig();
			LOG.info("Loaded gitolite configuration { "
					+ "repos: " + config.getRepositories().size() + ", "
					+ "groups: " + config.getGroups().size() + ", "
					+ "users: " + config.getUsers().size() + " }");
		} catch (final IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		bind(ConfigManager.class).toInstance(manager);
	}

}
