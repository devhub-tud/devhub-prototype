package nl.tudelft.ewi.dea.security;

import nl.tudelft.ewi.dea.di.SecurityModule;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.SimpleByteSource;

import com.google.common.annotations.VisibleForTesting;

public class UserFactory {

	public User createUser(final String mail, final String displayName, final String netid, final Integer studentNumber, final String plainPassword) {
		final String userSalt = SaltTool.generateSalt();
		final String hashedPassword = hashPassword(plainPassword, userSalt);
		return new User(displayName, mail, netid, studentNumber, userSalt, hashedPassword, UserRole.USER);
	}

	/**
	 * Reset the password for the given {@link User}.
	 */
	public void resetUserPassword(final User user, final String plainTextPassword) {
		if (plainTextPassword == null || plainTextPassword.length() < 8) {
			throw new IllegalArgumentException("The minimum password length is 8!");
		}

		String hashedPassword = hashPassword(plainTextPassword, user.getSalt());
		user.setPassword(hashedPassword);
	}

	@VisibleForTesting
	String hashPassword(final String plainPassword, final String userSalt) {
		final SimpleByteSource totalSalt = SaltTool.getFullSalt(userSalt);
		final int iterations = SecurityModule.NUMBER_OF_HASH_ITERATIONS;
		Sha256Hash hash = new Sha256Hash(plainPassword, totalSalt, iterations);
		return hash.toHex();
	}

}
