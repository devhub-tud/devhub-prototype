package nl.tudelft.ewi.dea.security;

import nl.tudelft.ewi.dea.di.SecurityModule;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.SimpleByteSource;

import com.google.common.annotations.VisibleForTesting;

public class UserFactory {

	public User createUser(String mail, String displayName, String plainPassword) {
		String userSalt = SaltTool.generateSalt();
		String hashedPassword = hashPassword(plainPassword, userSalt);
		User user = new User(displayName, mail, userSalt, hashedPassword, UserRole.USER);
		return user;
	}

	@VisibleForTesting
	String hashPassword(String plainPassword, String userSalt) {
		SimpleByteSource totalSalt = SaltTool.getFullSalt(userSalt);
		int iterations = SecurityModule.NUMBER_OF_HASH_ITERATIONS;
		Sha256Hash hash = new Sha256Hash(plainPassword, totalSalt, iterations);
		// Loop starts at 1 because it has already been hashed once in the
		// constructor.
		for (int i = 1; i < SecurityModule.NUMBER_OF_HASH_ITERATIONS; i++) {
			hash = new Sha256Hash(plainPassword, totalSalt, iterations);
		}
		return hash.toHex();
	}
}
