package nl.tudelft.ewi.dea.mail.templates;

import static nl.tudelft.ewi.dea.mail.CommonTestData.MAIL_PROPS;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import nl.tudelft.ewi.dea.mail.SimpleMessage;
import nl.tudelft.ewi.dea.mail.TestFactory;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class PasswordResetMailFactoryTest {

	private PasswordResetMailFactory factory;

	@Before
	public void setup() {
		factory = new PasswordResetMailFactory(TestFactory.getVelocityEngine(), MAIL_PROPS);
	}

	@Test
	public void verifyThatRegistrationmailIsCreatedCorrectly() throws IOException {
		String mailAddress = "test@user.com";
		SimpleMessage generatedMessage = factory.newMail(mailAddress, "html://verify.this.com");

		String expextedTemplate = Resources.toString(Resources.getResource("passwordResetMailSample.txt"), Charsets.UTF_8);

		assertThat(generatedMessage.body, is(expextedTemplate));
		assertThat(generatedMessage.from, is(MAIL_PROPS.getFrom()));
		assertThat(generatedMessage.to.size(), is(1));
		assertThat(generatedMessage.to, hasItem(mailAddress));
	}
}
