package nl.tudelft.ewi.dea.mail.internals;

import static com.google.common.collect.ImmutableList.copyOf;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import nl.tudelft.ewi.dea.dao.UnsentMailDao;
import nl.tudelft.ewi.dea.mail.MailException;
import nl.tudelft.ewi.dea.mail.MailModule.MailQueue;
import nl.tudelft.ewi.dea.mail.MailModule.SMTP;
import nl.tudelft.ewi.dea.mail.MailProperties;
import nl.tudelft.ewi.dea.mail.SimpleMessage;
import nl.tudelft.ewi.dea.model.UnsentMailAsJson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

/**
 * Eats the queue from {@link QueuedMailSender}.
 * 
 */
@Singleton
class MailQueueTaker implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(MailQueueTaker.class);
	private static final int TIME_OUT_IN_MIN = 5;

	private final BlockingQueue<UnsentMail> mailQueue;
	private final MailProperties mailProps;
	private final Transport transport;
	private final Session session;
	private final Provider<UnsentMailDao> unsentMailDao;
	private Provider<ObjectMapper> mapper;

	@Inject
	MailQueueTaker(@MailQueue BlockingQueue<UnsentMail> mailQueue, @SMTP Transport transport,
			MailProperties mailProps, Session session, Provider<UnsentMailDao> unsentMailDao, Provider<ObjectMapper> mapper) {
		this.mailQueue = mailQueue;
		this.transport = transport;
		this.mailProps = mailProps;
		this.session = session;
		this.unsentMailDao = unsentMailDao;
		this.mapper = mapper;
	}

	@Override
	public void run() {
		try {
			testConnection();
			checkForUnsentMails();
			while (!Thread.interrupted()) {
				LOG.debug("Waiting for messages to send");
				List<UnsentMail> messagesToSend = new LinkedList<>();
				messagesToSend.add(mailQueue.take());
				mailQueue.drainTo(messagesToSend);
				sendMessages(copyOf(messagesToSend));
			}
			throw new InterruptedException();
		} catch (InterruptedException e) {
			LOG.info("Mail sending queue was stopped. Unsent mails: {}. These are lost forever.", mailQueue.size());
		} catch (Throwable throwable) {
			LOG.error("Mail sending thread was stopped. This is a severe warning!", throwable);
			throw new MailException("Unexpected stop of the Mail queue taker", throwable);
		}
	}

	private void checkForUnsentMails() throws InterruptedException {
		// Wait for the DB to have an activate connection.
		List<UnsentMailAsJson> unsentMails = unsentMailDao.get().getUnsentMails();
		LOG.info("Found {} unsent mails to send.", unsentMails.size());
		ObjectMapper jsonMapper = mapper.get();
		for (UnsentMailAsJson unsentMail : unsentMails) {
			try {
				SimpleMessage message = jsonMapper.readValue(unsentMail.getMail(), SimpleMessage.class);
				mailQueue.add(new UnsentMail(unsentMail.getId(), message));
			} catch (IOException e) {
				LOG.warn("Could not deserialized a message. It will be lost forever: {}", unsentMail);
				unsentMailDao.get().remove(unsentMail.getId());
			}
		}
	}

	private void testConnection() {
		LOG.info("Testing SMTP connection");
		try {
			transport.connect(mailProps.getHost(), mailProps.getUser(), mailProps.getPassword());
			transport.close();
			LOG.info("SMTP connection successful");
		} catch (MessagingException e) {
			throw new MailException("Error while trying to connect to the SMTP service", e);
		}
	}

	@VisibleForTesting
	void sendMessages(ImmutableList<UnsentMail> messagesToSend) throws MessagingException {
		try {
			tryToSendMessages(messagesToSend);
		} catch (SendFailedException e) {
			tryAgainAfterDelay(messagesToSend, e);
		} catch (MessagingException e) {
			throw e;
		}
	}

	private void tryToSendMessages(ImmutableList<UnsentMail> messagesToSend) throws MessagingException {
		LOG.debug("Connecting to SMTP server");
		transport.connect(mailProps.getHost(), mailProps.getUser(), mailProps.getPassword());
		LOG.debug("Connected, sending messages");
		for (UnsentMail message : messagesToSend) {
			MimeMessage mimeMessage = message.getMessage().asMimeMessage(session);
			transport.sendMessage(message.getMessage().asMimeMessage(session), mimeMessage.getAllRecipients());
			unsentMailDao.get().remove(message.getId());
		}
		LOG.debug("Closing SMTP server");
		transport.close();
	}

	@VisibleForTesting
	void tryAgainAfterDelay(ImmutableList<UnsentMail> messagesToSend, SendFailedException e)
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
