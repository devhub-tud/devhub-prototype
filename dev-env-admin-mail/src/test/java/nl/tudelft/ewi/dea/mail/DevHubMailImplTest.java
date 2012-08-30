package nl.tudelft.ewi.dea.mail;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.tudelft.ewi.dea.mail.internals.MailSender;
import nl.tudelft.ewi.dea.mail.templates.VerifyRegistrationMailFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DevHubMailImplTest {

	@Mock private MailSender mailSender;
	@Mock private VerifyRegistrationMailFactory verifyRegMFactory;
	@InjectMocks private DevHubMailImpl devHubMail;

	@Test
	public void whenSentVerifyMailTheMailIsContructedAndSent() {
		String mailto = "test@test.com";
		String token = "http://kerse.pit.com";
		SimpleMessage message = mock(SimpleMessage.class);
		when(verifyRegMFactory.newMail(mailto, token)).thenReturn(message);

		devHubMail.sendVerifyRegistrationMail(mailto, token);

		verify(mailSender).deliver(message);
	}

}
