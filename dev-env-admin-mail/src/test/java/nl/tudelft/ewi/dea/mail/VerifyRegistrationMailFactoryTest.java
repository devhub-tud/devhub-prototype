package nl.tudelft.ewi.dea.mail;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.Guice;

public class VerifyRegistrationMailFactoryTest {

	@Test
	public void test() throws IOException {
		VerifyRegistrationMailFactory factory = Guice.createInjector(new MailModule()).getInstance(VerifyRegistrationMailFactory.class);
		String generatedTemplate = factory.newMail("test@user.com", "html://verify.this.com");

		String expextedTemplate = Resources.toString(Resources.getResource("verifyRegistrationMailSample.txt"), Charsets.UTF_8);
		System.out.println(generatedTemplate);
		;
		assertThat(generatedTemplate, is(expextedTemplate));
	}
}
