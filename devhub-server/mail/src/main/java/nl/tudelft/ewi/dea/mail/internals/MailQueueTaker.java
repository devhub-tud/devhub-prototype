package nl.tudelft.ewi.dea.mail.internals;

import static com.google.common.collect.ImmutableList.copyOf;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import nl.tudelft.ewi.dea.dao.UnsentMailDao;
import nl.tudelft.ewi.dea.mail.MailException;
import nl.tudelft.ewi.dea.mail.MailModule.MailQueue;
import nl.tudelft.ewi.dea.mail.MailModule.SMTP;
import nl.tudelft.ewi.dea.mail.MailProperties;
import nl.tudelft.ewi.dea.mail.SimpleMessage;
import nl.tudelft.ewi.dea.metrics.MetricGroup;
import nl.tudelft.ewi.dea.model.UnsentMailAsJson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.MetricsRegistry;

/**
 * Eats the queue from {@link QueuedMailSender}.
 * 
 */
@Singleton
class MailQueueTaker implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(MailQueueTaker.class);

	private final BlockingQueue<UnsentMail> mailQueue;
	private final MailProperties mailProps;
	private final Transport transport;
	private final Session session;
	private final UnsentMailDao unsentMailDao;
	private final Provider<ObjectMapper> mapper;

	private final Counter mailCounter;

	@Inject
	MailQueueTaker(@MailQueue BlockingQueue<UnsentMail> mailQueue, @SMTP Transport transport,
			MailProperties mailProps, Session session, UnsentMailDao unsentMailDao,
			Provider<ObjectMapper> mapper, MetricsRegistry metrics) {

		this.mailQueue = mailQueue;
		this.transport = transport;
		this.mailProps = mailProps;
		this.session = session;
		this.unsentMailDao = unsentMailDao;
		this.mapper = mapper;

		mailCounter = metrics.newCounter(MetricGroup.Mail.newName("Mails sent"));
	}

	@Override
	public void run() {
		try {
			testConnection();
			checkForUnsentMails();

			while (!Thread.interrupted()) {
				LOG.debug("Waiting for messages to send");
				List<UnsentMail> messagesToSend = Lists.newArrayList();
				messagesToSend.add(mailQueue.take());
				mailQueue.drainTo(messagesToSend);
				sendMessages(copyOf(messagesToSend));
			}
			LOG.info("Mail sending queue was stopped. Unsent mails: {}. These are lost forever.", mailQueue.size());

		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void checkForUnsentMails() throws InterruptedException {
		// Wait for the DB to have an activate connection.
		List<UnsentMailAsJson> unsentMails = unsentMailDao.findAll();
		LOG.info("Found {} unsent mails to send.", unsentMails.size());
		ObjectMapper jsonMapper = mapper.get();
		for (UnsentMailAsJson unsentMail : unsentMails) {
			try {
				SimpleMessage message = jsonMapper.readValue(unsentMail.getMail(), SimpleMessage.class);
				mailQueue.add(new UnsentMail(unsentMail.getId(), message));
			} catch (IOException e) {
				LOG.warn("Could not deserialized a message. It will be lost forever: {}", unsentMail);
				unsentMailDao.remove(unsentMail.getId());
			}
		}
	}

	@VisibleForTesting
	protected void testConnection() {
		LOG.info("Testing SMTP connection");
		try {
			transport.connect(mailProps.getHost(), mailProps.getUser(), mailProps.getPassword());
			transport.close();
			LOG.info("SMTP connection successful");
		} catch (MessagingException e) {
			throw new MailException("Error while trying to connect to the SMTP service", e);
		}
	}

	private void sendMessages(ImmutableList<UnsentMail> messagesToSend) throws MessagingException {
		LOG.debug("Connecting to SMTP server");
		transport.connect(mailProps.getHost(), mailProps.getUser(), mailProps.getPassword());
		LOG.debug("Connected, sending messages");
		for (UnsentMail message : messagesToSend) {
			MimeMessage mimeMessage = message.getMessage().asMimeMessage(session);
			transport.sendMessage(message.getMessage().asMimeMessage(session), mimeMessage.getAllRecipients());
			unsentMailDao.remove(message.getId());
			mailCounter.inc();
		}

		LOG.debug("Closing SMTP server");
		transport.close();
	}

}
