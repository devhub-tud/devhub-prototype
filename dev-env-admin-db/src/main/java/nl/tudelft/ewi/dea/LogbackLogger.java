package nl.tudelft.ewi.dea;

import liquibase.logging.LogLevel;
import liquibase.logging.core.AbstractLogger;
import liquibase.logging.core.DefaultLogger;
import liquibase.servicelocator.LiquibaseService;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

@LiquibaseService
public class LogbackLogger extends AbstractLogger {

	private ch.qos.logback.classic.Logger LOG;

	@Override
	public int getPriority() {
		return DefaultLogger.PRIORITY_DATABASE + 1;
	}

	@Override
	public void setName(final String p_name) {
		LOG = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(p_name);
	}

	@Override
	public LogLevel getLogLevel() {
		Level level = LOG.getLevel();
		LogLevel logLevel = LogLevel.INFO;
		switch (level.toInt()) {
			case Level.OFF_INT:
				logLevel = LogLevel.OFF;
				break;
			case Level.DEBUG_INT:
				logLevel = LogLevel.DEBUG;
				break;
			case Level.INFO_INT:
				logLevel = LogLevel.INFO;
				break;
			case Level.WARN_INT:
				logLevel = LogLevel.WARNING;
				break;
			case Level.ERROR_INT:
				logLevel = LogLevel.SEVERE;
				break;
			case Level.ALL_INT:
				logLevel = LogLevel.DEBUG;
				break;
		}
		return logLevel;
	}

	@Override
	public void setLogLevel(final String p_level) {
		final LogLevel level = LogLevel.valueOf(p_level.toUpperCase());
		setLogLevel(level);
	}

	@Override
	public void setLogLevel(final LogLevel p_level) {
		switch (p_level) {
			case DEBUG:
				LOG.setLevel(Level.DEBUG);
				break;
			case INFO:
				LOG.setLevel(Level.INFO);
				break;
			case OFF:
				LOG.setLevel(Level.OFF);
				break;
			case WARNING:
				LOG.setLevel(Level.WARN);
				break;
			case SEVERE:
				LOG.setLevel(Level.ERROR);
				break;
			default:
				LOG.setLevel(Level.INFO);
		}
	}

	@Override
	public void setLogLevel(final String p_logLevel, final String p_logFile) {
		setLogLevel(p_logLevel);
	}

	@Override
	public void severe(final String p_message) {
		LOG.error(p_message);
	}

	@Override
	public void severe(final String p_message, final Throwable p_e) {
		LOG.error(p_message, p_e);
	}

	@Override
	public void warning(final String p_message) {
		LOG.warn(p_message);
	}

	@Override
	public void warning(final String p_message, final Throwable p_e) {
		LOG.warn(p_message, p_e);
	}

	@Override
	public void info(final String p_message) {
		LOG.info(p_message);
	}

	@Override
	public void info(final String p_message, final Throwable p_e) {
		LOG.info(p_message, p_e);
	}

	@Override
	public void debug(final String p_message) {
		LOG.debug(p_message);
	}

	@Override
	public void debug(final String p_message, final Throwable p_e) {
		LOG.debug(p_message, p_e);
	}

}
