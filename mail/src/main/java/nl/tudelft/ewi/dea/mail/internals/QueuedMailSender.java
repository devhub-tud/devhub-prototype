package nl.tudelft.ewi.dea.mail.internals;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import nl.tudelft.ewi.dea.mail.MailModule.MailQueue;
import nl.tudelft.ewi.dea.mail.SimpleMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class QueuedMailSender implements MailSender {

	private static final Logger LOG = LoggerFactory.getLogger(QueuedMailSender.class);
	private final BlockingQueue<SimpleMessage> mailsToSend;

	@Inject
	QueuedMailSender(final ExecutorService executor, final MailQueueTaker taker, @MailQueue final BlockingQueue<SimpleMessage> mailqueue) {
		mailsToSend = mailqueue;

		LOG.info("Testing SMTP connection");
		taker.testConnection();
		LOG.info("SMTP connection successful");

		LOG.info("Scheduling the thread comsumer for excecutions.");
		executor.execute(taker);

	}

	@Override
	public void deliver(final SimpleMessage message) {
		LOG.debug("Adding message {} to queue: {}", message, mailsToSend.hashCode());
		try {
			mailsToSend.put(message);
		} catch (final InterruptedException e) {
			LOG.error("Could not schedule message. The thread was stopped before it could be added to the queue");
		}
	}

}
