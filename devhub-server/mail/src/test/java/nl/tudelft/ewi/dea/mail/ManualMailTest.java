package nl.tudelft.ewi.dea.mail;

import java.util.concurrent.TimeUnit;

import nl.tudelft.ewi.dea.mail.internals.CommonTestData;

import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

@Ignore("Only run this manually and fill in your own SMTP server.")
public class ManualMailTest {

	@Test
	public void testSendingAnEmailTwice() throws InterruptedException {
		MailProperties props = CommonTestData.MAIL_PROPS;
		Injector module = Guice.createInjector(new MailModule(props));
		DevHubMail mail = module.getInstance(DevHubMail.class);
		mail.sendVerifyRegistrationMail(props.getFrom(), "http://kers.nu");

		Thread.sleep(TimeUnit.SECONDS.toMillis(5));

		mail.sendVerifyRegistrationMail(props.getFrom(), "http://kers.blaat.nu");

		Thread.sleep(TimeUnit.SECONDS.toMillis(5));
	}

}
