package nl.tudelft.ewi.dea.di;

import java.io.IOException;

import javax.naming.ServiceUnavailableException;

import nl.minicom.gitolite.manager.ConfigManager;
import nl.minicom.gitolite.manager.git.PassphraseCredentialsProvider;
import nl.minicom.gitolite.manager.models.Config;
import nl.tudelft.ewi.dea.DevHubException;
import nl.tudelft.ewi.dea.ServerConfig;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

public class ProvisioningModule extends AbstractModule {

	private static final Logger LOG = LoggerFactory.getLogger(ProvisioningModule.class);
	private ServerConfig serverConfig;

	public ProvisioningModule(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	@Override
	protected void configure() {
		CredentialsProvider passPhrase = new PassphraseCredentialsProvider(serverConfig.getSshPassPhrase());
		final ConfigManager manager = ConfigManager.create(serverConfig.getGitoliteUrl(), passPhrase);

		try {
			final Config config = manager.getConfig();
			LOG.info("Loaded gitolite configuration { "
					+ "repos: " + config.getRepositories().size() + ", "
					+ "groups: " + config.getGroups().size() + ", "
					+ "users: " + config.getUsers().size() + " }");
		} catch (final IOException e) {
			throw new DevHubException(e.getMessage(), e);
		} catch (ServiceUnavailableException e) {
			throw new DevHubException("Could not configure the provisioning module: " + e.getMessage(), e);
		}

		bind(ConfigManager.class).toInstance(manager);
	}

}
