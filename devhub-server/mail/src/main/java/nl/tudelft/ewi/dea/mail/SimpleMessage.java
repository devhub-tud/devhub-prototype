package nl.tudelft.ewi.dea.mail;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.copyOf;

import java.util.Set;

import javax.annotation.concurrent.Immutable;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.Data;

import com.google.common.collect.ImmutableSet;

/**
 * Wrapper for a simple mail message. It is immutable an can be used to extract
 * a {@link MimeMessage} for javax.mail stuff.
 * 
 */
@Data
@Immutable
public class SimpleMessage {

	public final ImmutableSet<String> to;
	private final String subject;
	private final String body;
	private final String from;

	public SimpleMessage(Set<String> to, String subject, String body, String from) {
		checkArgument(to != null && !to.isEmpty(), "More then one TO address required");
		this.from = checkNotNull(from);
		this.to = copyOf(to);
		this.subject = checkNotNull(subject);
		this.body = checkNotNull(body);
	}

	public SimpleMessage(String to, String subject, String body, String from) {
		this(ImmutableSet.of(checkNotNull(to)), subject, body, from);
	}

	public MimeMessage asMimeMessage(Session session) {
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(asAddress(from));
			for (String toAddress : to) {
				message.addRecipient(Message.RecipientType.TO, asAddress(toAddress));
			}
			message.setSubject(subject, "UTF-8");
			message.setText(body, "UTF-8");
			return message;
		} catch (MessagingException e) {
			throw new MailException("Unexpected error while creating message: " + toString(), e);
		}
	}

	private Address asAddress(String toAddress) {
		try {
			return new InternetAddress(toAddress, true);
		} catch (AddressException e) {
			throw new MailException("Address error for from address: " + from + " in email: " + toString(), e);
		}
	}
}
