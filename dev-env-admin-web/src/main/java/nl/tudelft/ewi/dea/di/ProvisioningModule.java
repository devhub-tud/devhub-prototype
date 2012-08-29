package nl.tudelft.ewi.dea.di;

import nl.minicom.gitolite.manager.ConfigManager;

import com.google.inject.AbstractModule;

public class ProvisioningModule extends AbstractModule {

	@Override
	protected void configure() {
		ConfigManager configManager = ConfigManager.create("git@dea.hartveld.com:gitolite-admin");
		bind(ConfigManager.class).toInstance(configManager);
	}

}
