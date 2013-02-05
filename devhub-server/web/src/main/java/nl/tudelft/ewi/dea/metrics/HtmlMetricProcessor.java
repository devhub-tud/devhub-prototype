package nl.tudelft.ewi.dea.metrics;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Metered;
import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricProcessor;
import com.yammer.metrics.core.Timer;

/**
 * Processes metrics into a HTML table.
 * 
 */
public class HtmlMetricProcessor implements MetricProcessor<StringBuilder> {

	private static final DecimalFormat DECIMALS = new DecimalFormat("#.##");

	private void process(MetricName name, Object value, StringBuilder context) {
		context.append("<tr><td>").append(name.getName()).append("</td><td>").append(value).append("</td></tr>");
	}

	/**
	 * @param metrics The metrics you want to process
	 * @return A string of table rows.
	 */
	public String processAll(Map<MetricName, Metric> metrics) {
		StringBuilder sb = new StringBuilder();
		for (Entry<MetricName, Metric> metric : metrics.entrySet()) {
			if (metric.getValue() instanceof Metered) {
				if (metric.getValue() instanceof Timer) {
					processTimer(metric.getKey(), (Timer) metric.getValue(), sb);
				} else {
					processMeter(metric.getKey(), (Metered) metric.getValue(), sb);
				}
			} else if (metric.getValue() instanceof Counter) {
				processCounter(metric.getKey(), (Counter) metric.getValue(), sb);
			} else if (metric.getValue() instanceof Histogram) {
				processHistogram(metric.getKey(), (Histogram) metric.getValue(), sb);
			} else if (metric.getValue() instanceof Gauge) {
				processGauge(metric.getKey(), (Gauge<?>) metric.getValue(), sb);
			} else {
				throw new IllegalStateException("Unknown metric " + metric);
			}
		}
		return sb.toString();
	}

	@Override
	public void processMeter(MetricName name, Metered meter, StringBuilder context) {
		String value = toString(meter.meanRate(), meter.rateUnit());
		process(name, value, context);
	}

	@Override
	public void processCounter(MetricName name, Counter counter, StringBuilder context) {
		process(name, counter.count(), context);
	}

	@Override
	public void processHistogram(MetricName name, Histogram histogram, StringBuilder context) {
		throw new IllegalStateException("Not implemented yet");
	}

	@Override
	public void processTimer(MetricName name, Timer timer, StringBuilder context) {
		String value = "Mean: " + toString(timer.mean(), timer.durationUnit())
				+ ", Max: " + toString(timer.max(), timer.durationUnit())
				+ ", Min: " + toString(timer.min(), timer.durationUnit());
		process(name, value, context);
	}

	@Override
	public void processGauge(MetricName name, Gauge<?> gauge, StringBuilder context) {
		process(name, gauge.value(), context);
	}

	private String toString(double value, TimeUnit unit) {
		switch (unit) {
			case DAYS:
				return DECIMALS.format(value) + " per day";
			case HOURS:
				return DECIMALS.format(value) + " per hour";
			case MILLISECONDS:
				return DECIMALS.format(value) + " per ms";
			case MINUTES:
				return DECIMALS.format(value) + " per minute";
			case SECONDS:
				return DECIMALS.format(value) + " per second";
			default:
				return DECIMALS.format(value) + " per " + unit;
		}

	}
}
