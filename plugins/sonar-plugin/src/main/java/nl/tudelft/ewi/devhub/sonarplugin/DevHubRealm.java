package nl.tudelft.ewi.devhub.sonarplugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.security.Authenticator;
import org.sonar.api.security.ExternalGroupsProvider;
import org.sonar.api.security.ExternalUsersProvider;
import org.sonar.api.security.SecurityRealm;

public class DevHubRealm extends SecurityRealm {

	private static final Logger LOG = LoggerFactory.getLogger(DevHubRealm.class);

	private static final String DEV_HUB_URL_KEY = "devhub.url";

	public DevHubRealm(Settings settings) {
		LOG.info("Initialized with settings {}", settings.getString(DEV_HUB_URL_KEY));
	}

	@Override
	public String getName() {
		return "DevHubRealm";
	}

	@Override
	public Authenticator doGetAuthenticator() {
		return new Authenticator() {

			@Override
			public boolean doAuthenticate(Context context) {
				LOG.warn("Received username={} password={}", context.getUsername(), context.getPassword());
				return "test".equals(context.getPassword())
						&& "test".equals(context.getUsername());
			}

		};
	}

	@Override
	public ExternalUsersProvider getUsersProvider() {
		return new UserProvider();
	}

	@Override
	public ExternalGroupsProvider getGroupsProvider() {
		return new GroupProvider();
	}

}
