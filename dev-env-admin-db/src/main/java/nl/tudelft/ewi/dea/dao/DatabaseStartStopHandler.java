package nl.tudelft.ewi.dea.dao;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.PersistService;

@Singleton
public class DatabaseStartStopHandler {

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseStartStopHandler.class);
	private final PersistService persistService;
	private final Lock startStopLock = new ReentrantLock();

	@Inject
	DatabaseStartStopHandler(PersistService persistService) {
		this.persistService = persistService;
	}

	/**
	 * Start the underlying persistence layer.
	 */
	public void start() {
		try {
			startStopLock.lockInterruptibly();
			LOG.info("Starting the persistence layer");
			persistService.start();
			LOG.debug("Persistence layer started");
			startStopLock.unlock();
		} catch (InterruptedException e) {
			LOG.warn("Database start was cancelled before it started.", e);
		}
	}

	/**
	 * Stop the underlying persistence layer.
	 */
	public void stop() {
		LOG.info("Stopping the persistence layer");
		startStopLock.lock();
		persistService.stop();
		LOG.debug("Persistence layer stopped");
		startStopLock.unlock();
	}
}
