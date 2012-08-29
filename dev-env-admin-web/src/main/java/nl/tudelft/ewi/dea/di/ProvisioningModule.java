package nl.tudelft.ewi.dea.di;

import nl.minicom.gitolite.manager.ConfigManager;
import nl.minicom.gitolite.manager.git.PassphraseCredentialsProvider;

import com.google.inject.AbstractModule;

public class ProvisioningModule extends AbstractModule {

	@Override
	protected void configure() {
		ConfigManager configManager = ConfigManager.create("git@dea.hartveld.com:gitolite-admin", new PassphraseCredentialsProvider("AXovmeBy1"));
		bind(ConfigManager.class).toInstance(configManager);
	}

}
