package nl.tudelft.ewi.dea.mail;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.tudelft.ewi.dea.mail.internals.MailSender;
import nl.tudelft.ewi.dea.mail.templates.InviteProjectFactory;
import nl.tudelft.ewi.dea.mail.templates.PasswordResetMailFactory;
import nl.tudelft.ewi.dea.mail.templates.ServiceRegistrationMailFactory;
import nl.tudelft.ewi.dea.mail.templates.VerifyRegistrationMailFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Provider;

@RunWith(MockitoJUnitRunner.class)
public class DevHubMailImplTest {

	@Mock private MailSender mailSender;
	@Mock private VerifyRegistrationMailFactory verifyRegMailFact;
	@Mock private Provider<VerifyRegistrationMailFactory> verifyRegMailFactProv;
	@Mock private Provider<PasswordResetMailFactory> passwResetFactoryProv;
	@Mock private Provider<ServiceRegistrationMailFactory> serviceRegistrationFac;
	@Mock private Provider<InviteProjectFactory> inviteProjectFac;
	@Mock private PasswordResetMailFactory passwResetFactory;

	private DevHubMailImpl devHubMail;

	@Before
	public void setup() {
		when(verifyRegMailFactProv.get()).thenReturn(verifyRegMailFact);
		when(passwResetFactoryProv.get()).thenReturn(passwResetFactory);
		devHubMail = new DevHubMailImpl(verifyRegMailFactProv, passwResetFactoryProv, mailSender,
				serviceRegistrationFac, inviteProjectFac);
	}

	@Test
	public void whenSentVerifyMailTheMailIsContructedAndSent() {
		String mailto = "test@test.com";
		String token = "http://kerse.pit.com";
		SimpleMessage message = mock(SimpleMessage.class);
		when(verifyRegMailFact.newMail(mailto, token)).thenReturn(message);

		devHubMail.sendVerifyRegistrationMail(mailto, token);

		verify(mailSender).deliver(message);
	}

}
