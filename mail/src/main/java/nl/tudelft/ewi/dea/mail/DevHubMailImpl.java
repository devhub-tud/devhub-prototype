package nl.tudelft.ewi.dea.mail;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.mail.internals.MailSender;
import nl.tudelft.ewi.dea.mail.templates.VerifyRegistrationMailFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link DevHubMail}.
 */
class DevHubMailImpl implements DevHubMail {

	private static final Logger LOG = LoggerFactory.getLogger(DevHubMailImpl.class);

	private final VerifyRegistrationMailFactory verifyRegMailFactory;
	private final MailSender sender;

	@Inject
	DevHubMailImpl(VerifyRegistrationMailFactory verifyRegMailFactory, MailSender sender) {
		this.verifyRegMailFactory = verifyRegMailFactory;
		this.sender = sender;
	}

	@Override
	public void sendVerifyRegistrationMail(String toAdress, String verifyUrl) {
		// TODO validate addresses and URL's.
		LOG.debug("Sending registration mail to address {} with url {}", toAdress, verifyUrl);
		SimpleMessage message = verifyRegMailFactory.newMail(toAdress, verifyUrl);
		sender.deliver(message);
	}
}
