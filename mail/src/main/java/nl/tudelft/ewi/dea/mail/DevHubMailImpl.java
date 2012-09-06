package nl.tudelft.ewi.dea.mail;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.mail.internals.MailSender;
import nl.tudelft.ewi.dea.mail.templates.PasswordResetMailFactory;
import nl.tudelft.ewi.dea.mail.templates.VerifyRegistrationMailFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;

/**
 * Default implementation of {@link DevHubMail}.
 */
class DevHubMailImpl implements DevHubMail {

	private static final Logger LOG = LoggerFactory.getLogger(DevHubMailImpl.class);

	private final Provider<VerifyRegistrationMailFactory> verifyRegMailFactory;
	private final Provider<PasswordResetMailFactory> passwordResetFac;
	private final MailSender sender;

	@Inject
	public DevHubMailImpl(Provider<VerifyRegistrationMailFactory> verifyRegMailFactory, Provider<PasswordResetMailFactory> passwordResetFac, MailSender sender) {
		this.verifyRegMailFactory = verifyRegMailFactory;
		this.passwordResetFac = passwordResetFac;
		this.sender = sender;
	}

	@Override
	public void sendVerifyRegistrationMail(String toAdress, String verifyUrl) {
		// TODO validate addresses and URL's.
		LOG.debug("Sending registration mail to address {} with url {}", toAdress, verifyUrl);
		SimpleMessage message = verifyRegMailFactory.get().newMail(toAdress, verifyUrl);
		sender.deliver(message);
	}

	@Override
	public void sendResetPasswordMail(String toAdress, String url) {
		LOG.debug("Sending reset password mail to {} with url {}", toAdress, url);
		passwordResetFac.get().newMail(toAdress, url);
	}
}
