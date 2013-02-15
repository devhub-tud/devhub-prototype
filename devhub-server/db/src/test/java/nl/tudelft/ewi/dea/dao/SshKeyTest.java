package nl.tudelft.ewi.dea.dao;

import nl.tudelft.ewi.dea.model.SshKey;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.model.UserRole;

import org.junit.Test;

public class SshKeyTest {

	private static final User VALID_USER = new User("test", "test@devhub.nl", "test", 1, "abcdef", "1234567890", UserRole.USER);
	private static final String VALID_NAME = "air-local";
	private static final String VALID_CONTENT = "ssh-rsa AAAAB3NzaC2yc2EAAAADAQABAAABAQDcUuP3Fm0K40ichOzof2ieDeRJneon+alcYMx2S2Etc3SPuwCl6wFV3HY1vno4NThiU7xaP7R6m8dyJNymAtNxuiYJmdkWXAobJlpodkUeI8rH6IY4dlbOovk8JNiM8kFo+6QwMTQaRPvrwocILtckH/yKGl/UEBVc0dIo5k87MlsrcWJKFU0iTvOaVfuK9EdYa3UMABHQWPZtOe1CA5RqScBFKrg+J3GyqbjVVR0FJD6+ZEP85Jt7GQGRPj6551SZaMY03HGajhDEHQ1npfywCZVjnGzAMg3Dpr08JuW+kuqVFn6u/BapnFtGY/VkQjQ/WGEUTBhDLf2wCqDWzi1J test@devhub.nl";

	@Test
	public void testValidSshKey() {
		new SshKey(VALID_USER, VALID_NAME, VALID_CONTENT);
	}

	@Test(expected = NullPointerException.class)
	public void testSshKeyWithNullAsUser() {
		new SshKey(null, VALID_NAME, VALID_CONTENT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSshKeyWithNullAsKeyName() {
		new SshKey(VALID_USER, null, VALID_CONTENT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSshKeyWithEmptyKeyName() {
		new SshKey(VALID_USER, "", VALID_CONTENT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSshKeyWithNullAsKeyContent() {
		new SshKey(VALID_USER, VALID_NAME, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSshKeyWithEmptyKeyContent() {
		new SshKey(VALID_USER, VALID_NAME, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSshKeyDoesNotAcceptSpacesInKeyName() {
		new SshKey(VALID_USER, "air local", VALID_CONTENT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSshKeyDoesNotAcceptAtInKeyName() {
		new SshKey(VALID_USER, "air@local", VALID_CONTENT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSshKeyWithInvalidContent() {
		new SshKey(VALID_USER, VALID_NAME, "ssh-rsa ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSshKeyThatDoesNotStartWithSshRSA() {
		new SshKey(VALID_USER, VALID_NAME, "ssh- dskf");
	}

}
