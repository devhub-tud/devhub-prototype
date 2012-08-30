package nl.tudelft.ewi.dea.mail;

public class CommonTestData {

	public static final MailProperties MAIL_PROPS =
			MailProperties.newWithAuth(
					"somehost", "someuser", "testpw",
					"fromtest", true, 123);

	public static final SimpleMessage SAMPLE_MESSAGE =
			new SimpleMessage("harry@potter.com", "test subject",
					"test body", "ron@potter.com");

}
