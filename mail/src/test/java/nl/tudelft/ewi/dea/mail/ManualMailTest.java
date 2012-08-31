package nl.tudelft.ewi.dea.mail;

import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

@Ignore("Only run this manually and fill in your own SMTP server.")
public class ManualMailTest {

	@Test
	public void testSendingAnEmailTwice() throws InterruptedException {
		String host = "smtp.gmail.com";
		String user = "";
		String password = "";
		int port = 587;
		boolean ssl = true;
		MailProperties props = MailProperties.newWithAuth(host, user, password, user, ssl, port);
		Injector module = Guice.createInjector(new MailModule(props));
		DevHubMail mail = module.getInstance(DevHubMail.class);
		mail.sendVerifyRegistrationMail(user, "http://kers.nu");

		Thread.sleep(TimeUnit.SECONDS.toMillis(5));

		mail.sendVerifyRegistrationMail(user, "http://kers.blaat.nu");

		Thread.sleep(TimeUnit.SECONDS.toMillis(5));
	}

}
