package nl.tudelft.ewi.dea.metrics;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricsRegistry;

public class InstrumentedSessionListener implements HttpSessionListener {

	private static final Logger LOG = LoggerFactory.getLogger(InstrumentedSessionListener.class);

	private final Counter sessionCounter;

	public InstrumentedSessionListener() {
		MetricsRegistry registry = Metrics.defaultRegistry();
		sessionCounter = registry.newCounter(new MetricName("Web", "", "Active sessions"));
	}

	@Override
	public void sessionCreated(HttpSessionEvent session) {
		LOG.info("Session created {}", session);
		sessionCounter.inc();
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent session) {
		LOG.info("Session destroyed {}", session);
		sessionCounter.dec();
	}

}
