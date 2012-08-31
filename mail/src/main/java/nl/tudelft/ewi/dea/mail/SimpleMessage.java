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

import com.google.common.collect.ImmutableSet;

/**
 * Wrapper for a simple mail message. It is immutable an can be used to extract
 * a {@link MimeMessage} for javax.mail stuff.
 * 
 */
@Immutable
public class SimpleMessage {

	public final ImmutableSet<String> to;
	public final String subject;
	public final String body;
	public final String from;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SimpleMessage other = (SimpleMessage) obj;
		if (body == null) {
			if (other.body != null) {
				return false;
			}
		} else if (!body.equals(other.body)) {
			return false;
		}
		if (subject == null) {
			if (other.subject != null) {
				return false;
			}
		} else if (!subject.equals(other.subject)) {
			return false;
		}
		if (to == null) {
			if (other.to != null) {
				return false;
			}
		} else if (!to.equals(other.to)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		String truncatedBody = body.replaceAll("\n", "\\\\n");
		return "Message [to=" + to + ", subject=" + subject + ", body=" + truncatedBody + "]";
	}

}
