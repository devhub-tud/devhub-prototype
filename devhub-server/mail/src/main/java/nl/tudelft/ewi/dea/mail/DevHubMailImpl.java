package nl.tudelft.ewi.dea.mail;

import javax.inject.Inject;

import nl.tudelft.ewi.dea.mail.internals.MailSender;
import nl.tudelft.ewi.dea.mail.templates.InviteProjectFactory;
import nl.tudelft.ewi.dea.mail.templates.PasswordResetMailFactory;
import nl.tudelft.ewi.dea.mail.templates.ServiceRegistrationMailFactory;
import nl.tudelft.ewi.dea.mail.templates.VerifyRegistrationMailFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;

/**
 * Default implementation of {@link DevHubMail}.
 */
class DevHubMailImpl implements DevHubMail {

	private static final String USER_FEEDBACK_SUBJECT_PREFIX = "[DevHub user feedback]";

	private static final Logger LOG = LoggerFactory.getLogger(DevHubMailImpl.class);

	private final Provider<VerifyRegistrationMailFactory> verifyRegMailFactory;
	private final Provider<PasswordResetMailFactory> passwordResetFac;
	private final Provider<InviteProjectFactory> inviteProjectFac;
	private final Provider<ServiceRegistrationMailFactory> serviceRegistrationFac;
	private final MailSender sender;

	@Inject
	public DevHubMailImpl(Provider<VerifyRegistrationMailFactory> verifyRegMailFactory,
			Provider<PasswordResetMailFactory> passwordResetFac, MailSender sender,
			Provider<ServiceRegistrationMailFactory> serviceRegistrationMailFactory,
			Provider<InviteProjectFactory> inviteProjectFac) {

		this.sender = sender;
		this.verifyRegMailFactory = verifyRegMailFactory;
		this.passwordResetFac = passwordResetFac;
		this.serviceRegistrationFac = serviceRegistrationMailFactory;
		this.inviteProjectFac = inviteProjectFac;
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
		SimpleMessage message = passwordResetFac.get().newMail(toAdress, url);
		sender.deliver(message);
	}

	@Override
	public void sendProjectInvite(String email, String displayName, String projectName, String url) {
		LOG.debug("Sending invitation mail to address {}", email);
		SimpleMessage mail = inviteProjectFac.get().sendProjectInvite(email, displayName, projectName, url);
		sender.deliver(mail);
	}

	@Override
	public void sendDevHubInvite(String email, String fromDisplayName, String projectName, String publicUrl) {
		LOG.debug("Sending DevHub invite to addres {}", email);
		SimpleMessage mail = inviteProjectFac.get().sendDevHubInvite(email, fromDisplayName, projectName, publicUrl);
		sender.deliver(mail);
	}

	@Override
	public void sendFeedbackEmail(String from, String to, String title, String content) {
		LOG.debug("Sending feedback email from address: {}", from);
		SimpleMessage mail = new SimpleMessage(to, USER_FEEDBACK_SUBJECT_PREFIX + title, content, from);
		sender.deliver(mail);
	}

	@Override
	public void sendServiceRegistrationEmail(String serviceName, String userName, String password, String email) {
		LOG.debug("Sending service registration email to address: {}", email);
		SimpleMessage mail = serviceRegistrationFac.get().sendServiceRegistrationMail(email, userName, password, serviceName);
		sender.deliver(mail);
	}
}
