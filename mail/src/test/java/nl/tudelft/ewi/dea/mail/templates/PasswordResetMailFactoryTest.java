package nl.tudelft.ewi.dea.mail.templates;

import static nl.tudelft.ewi.dea.mail.CommonTestData.MAIL_PROPS;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import nl.tudelft.ewi.dea.mail.SimpleMessage;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class PasswordResetMailFactoryTest {

	private PasswordResetMailFactory factory;

	@Before
	public void setup() {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		factory = new PasswordResetMailFactory(engine, MAIL_PROPS);
	}

	@Test
	public void verifyThatRegistrationmailIsCreatedCorrectly() throws IOException {
		String mailAddress = "test@user.com";
		SimpleMessage generatedMessage = factory.newMail(mailAddress, "html://verify.this.com");

		String expextedTemplate = Resources.toString(Resources.getResource("passwordResetMailSample.txt"), Charsets.UTF_8);

		assertThat(generatedMessage.body, is(expextedTemplate));
		assertThat(generatedMessage.from, is(MAIL_PROPS.from));
		assertThat(generatedMessage.to.size(), is(1));
		assertThat(generatedMessage.to, hasItem(mailAddress));
	}
}
