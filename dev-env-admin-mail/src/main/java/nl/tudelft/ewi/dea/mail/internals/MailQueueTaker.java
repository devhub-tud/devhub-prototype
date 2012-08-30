package nl.tudelft.ewi.dea.mail.internals;

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import nl.tudelft.ewi.dea.mail.MailException;
import nl.tudelft.ewi.dea.mail.MailModule.MailQueue;
import nl.tudelft.ewi.dea.mail.MailModule.SMTP;
import nl.tudelft.ewi.dea.mail.MailProperties;
import nl.tudelft.ewi.dea.mail.SimpleMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * Eats the queue from {@link QueuedMailSender}.
 * 
 */
@Singleton
class MailQueueTaker implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(MailQueueTaker.class);

	private static final int TIME_OUT_IN_MIN = 5;

	private final BlockingQueue<SimpleMessage> mailQueue;

	private final MailProperties mailProps;

	private final Transport transport;

	private Session session;

	@Inject
	MailQueueTaker(@MailQueue BlockingQueue<SimpleMessage> mailQueue, @SMTP Transport transport,
			MailProperties mailProps, Session session) {
		this.mailQueue = mailQueue;
		this.transport = transport;
		this.mailProps = mailProps;
		this.session = session;
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				LOG.debug("Waiting for messages to send");
				List<SimpleMessage> messagesToSend = new LinkedList<>();
				messagesToSend.add(mailQueue.take());
				mailQueue.drainTo(messagesToSend);
				sendMessages(copyOf(messagesToSend));
			}
			throw new InterruptedException();
		} catch (InterruptedException e) {
			LOG.info("Mail sending queue was stopped. Unsent mails: {}. These are lost forever.", mailQueue.size());
		} catch (Throwable throwable) {
			LOG.error("Mail sending thread was stopped. This is a severe warning!");
			throw new MailException("Unexpected stop of the Mail queue taker", throwable);
		}
	}

	void testConnection() {
		try {
			transport.connect(mailProps.host, mailProps.user, mailProps.password);
			transport.close();
		} catch (MessagingException e) {
			throw new MailException("Error while trying to connect to the SMTP service", e);
		}
	}

	private void sendMessages(ImmutableList<SimpleMessage> messagesToSend) throws MessagingException {
		try {
			tryToSendMessages(messagesToSend);
		} catch (SendFailedException e) {
			tryAgainAfterDelay(messagesToSend, e);
		} catch (MessagingException e) {
			throw e;
		}
	}

	private void tryToSendMessages(ImmutableList<SimpleMessage> messagesToSend) throws MessagingException {
		LOG.debug("Connecting to SMTP server");
		transport.connect(mailProps.host, mailProps.user, mailProps.password);
		LOG.debug("Connected, sending messages");
		for (SimpleMessage message : messagesToSend) {
			MimeMessage mimeMessage = message.asMimeMessage(session);
			transport.sendMessage(message.asMimeMessage(session), mimeMessage.getAllRecipients());
		}
		LOG.debug("Closing SMTP server");
		transport.close();
	}

	private void tryAgainAfterDelay(ImmutableList<SimpleMessage> messagesToSend, SendFailedException e)
			throws MessagingException {
		LOG.warn("Sending mail failed. Trying again in {} minutes. The error was: {}", TIME_OUT_IN_MIN, e.getMessage());
		LOG.debug("Full error print for debug: ", e);
		try {
			Thread.sleep(TimeUnit.MINUTES.toMillis(TIME_OUT_IN_MIN));
		} catch (InterruptedException e1) {
			throw new MailException("The mail thread was interrupted while it was waiting");
		}
		LOG.debug("Time out passed. Trying to send messages again...");
		sendMessages(messagesToSend);
	}

}