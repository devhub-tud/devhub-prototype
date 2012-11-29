package nl.tudelft.ewi.dea.mail.internals;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import nl.tudelft.ewi.dea.dao.UnsentMailDao;
import nl.tudelft.ewi.dea.mail.MailModule.MailQueue;
import nl.tudelft.ewi.dea.mail.SimpleMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
class QueuedMailSender implements MailSender {

	private static final Logger LOG = LoggerFactory.getLogger(QueuedMailSender.class);
	private final BlockingQueue<UnsentMail> mailsToSend;
	private final Provider<UnsentMailDao> unsentMailDao;
	private final ObjectMapper objectMapper;

	@Inject
	QueuedMailSender(ExecutorService executor, MailQueueTaker taker, @MailQueue BlockingQueue<UnsentMail> mailqueue
			, Provider<UnsentMailDao> unsentMailDao, ObjectMapper objectMapper) {
		mailsToSend = mailqueue;
		this.unsentMailDao = unsentMailDao;
		this.objectMapper = objectMapper;

		LOG.info("Scheduling the thread comsumer for excecutions.");
		executor.execute(taker);

	}

	@Override
	public void deliver(final SimpleMessage message) {
		LOG.debug("Adding message {} to queue: {}", message, mailsToSend.hashCode());
		try {
			mailsToSend.put(persistedMessage(message));
		} catch (final InterruptedException e) {
			LOG.error("Could not schedule message. The thread was stopped before it could be added to the queue");
		}
	}

	public UnsentMail persistedMessage(SimpleMessage message) {
		try {
			String mailAsJson = objectMapper.writeValueAsString(message);
			long id = unsentMailDao.get().persist(mailAsJson).getId();
			return new UnsentMail(id, message);
		} catch (JsonProcessingException e) {
			LOG.error("Skipping persistant storage for a message: Could not jsonize this message: " + message, e);
			return new UnsentMail(0, message);
		}

	}

}
