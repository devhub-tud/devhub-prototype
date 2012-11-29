package nl.tudelft.ewi.devhub.sonarplugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.security.ExternalUsersProvider;
import org.sonar.api.security.UserDetails;

public class UserProvider extends ExternalUsersProvider {

	private static final Logger LOG = LoggerFactory.getLogger(UserProvider.class);

	@Override
	public UserDetails doGetUserDetails(Context context) {
		UserDetails user = new UserDetails();
		user.setEmail("alex@nederlof.com");
		user.setName("Alex Nederlof");
		return user;
	}
}
