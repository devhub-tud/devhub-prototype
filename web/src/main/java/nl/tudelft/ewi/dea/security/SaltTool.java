package nl.tudelft.ewi.dea.security;

import java.util.Random;

import nl.tudelft.ewi.dea.model.User;

import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

final class SaltTool {

	static final String APPLICATION_SALT = "skjhdf9834hj";

	static ByteSource getFullSalt(User user) {
		return new SimpleByteSource(user.getSalt() + APPLICATION_SALT);
	}

	static String generateSalt() {
		return Integer.toHexString(new Random().nextInt());
	}

	public static SimpleByteSource getFullSalt(String userSalt) {
		return new SimpleByteSource(userSalt + APPLICATION_SALT);
	}

	private SaltTool() {}
}
