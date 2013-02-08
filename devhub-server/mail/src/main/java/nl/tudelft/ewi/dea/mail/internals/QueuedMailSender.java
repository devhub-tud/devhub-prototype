package nl.tudelft.ewi.dea.mail.internals;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
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
	private final UnsentMailDao unsentMailDao;
	private final ObjectMapper objectMapper;
	private final ScheduledThreadPoolExecutor executor;
	private final MailQueueTaker taker;

	private final AtomicBoolean initialized = new AtomicBoolean(false);

	@Inject
	QueuedMailSender(MailQueueTaker taker, @MailQueue BlockingQueue<UnsentMail> mailqueue
			, UnsentMailDao unsentMailDao, ObjectMapper objectMapper) {

		this.taker = taker;
		this.mailsToSend = mailqueue;
		this.unsentMailDao = unsentMailDao;
		this.objectMapper = objectMapper;

		this.executor = new ScheduledThreadPoolExecutor(1);
	}

	@Override
	public void initialize() {
		synchronized (initialized) {
			if (initialized.get()) {
				throw new IllegalStateException("MailSender is already initialized!");
			}

			LOG.info("Scheduling the thread comsumer for excecutions.");
			executor.scheduleWithFixedDelay(taker, 1, 1, TimeUnit.MINUTES);
			initialized.set(true);
		}
	}

	@Override
	public void shutdown() {
		executor.shutdown();
	}

	@Override
	public void deliver(final SimpleMessage message) {
		if (!initialized.get()) {
			throw new IllegalStateException("MailSender has not been initialized yet!");
		}

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
			long id = unsentMailDao.persist(mailAsJson).getId();
			return new UnsentMail(id, message);
		} catch (JsonProcessingException e) {
			LOG.error("Skipping persistant storage for a message: Could not jsonize this message: " + message, e);
			return new UnsentMail(0, message);
		}
	}

}
