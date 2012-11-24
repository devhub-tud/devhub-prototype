package nl.tudelft.ewi.dea.jaxrs.html;

import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.jaxrs.html.utils.Renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.inject.servlet.RequestScoped;
import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.core.VirtualMachineMetrics;

@RequestScoped
@Path("metrics")
@Produces(MediaType.TEXT_HTML)
public class MetricPage {

	private static final Logger LOG = LoggerFactory.getLogger(MetricPage.class);

	private static final DecimalFormat DECIMALS = new DecimalFormat("#.##");

	private final Renderer renderer;
	private MetricsRegistry registry;

	private VirtualMachineMetrics vmMetrics;

	@Inject
	MetricPage(Renderer renderer, MetricsRegistry registry, VirtualMachineMetrics vmMetrics) {
		this.renderer = renderer;
		this.registry = registry;
		this.vmMetrics = vmMetrics;
	}

	@GET
	public String servePage() {
		for (Entry<String, SortedMap<MetricName, Metric>> groupEntry : registry.groupedMetrics().entrySet()) {
			StringBuilder sb = new StringBuilder("Group=").append(groupEntry.getKey()).append('\n');
			for (Entry<MetricName, Metric> metric : groupEntry.getValue().entrySet()) {
				sb.append("name=").append(metric.getKey()).append(" metric is=").append(metric.getValue());
			}
			LOG.warn("\n" + sb.toString());
		}
		LOG.warn(vmMetrics.toString());
		ImmutableMap<Object, Object> vmMetricsMap = createVmMap();

		return renderer
				.setValue("vmMetrics", vmMetricsMap)
				.render("metrics.tpl");
	}

	private ImmutableMap<Object, Object> createVmMap() {
		return ImmutableMap.builder()
				.put("Name", vmMetrics.name())
				.put("Version", vmMetrics.version())
				.put("Up time", toReadableTime(vmMetrics.uptime()))
				.put("Thread count", vmMetrics.threadCount())
				.put("Deadlocked threads", vmMetrics.deadlockedThreads().size())
				.put("Memory available", inMegaBytes(vmMetrics.heapMax()))
				.put("Memory used", inMegaBytes(vmMetrics.heapUsed()))
				.put("Memory used percentage", DECIMALS.format(vmMetrics.heapUsage()) + '%')
				.build();
	}

	private Object toReadableTime(long seconds) {
		long days = TimeUnit.SECONDS.toDays(seconds);
		seconds -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.SECONDS.toHours(seconds);
		seconds -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.SECONDS.toMinutes(seconds);
		seconds -= TimeUnit.MINUTES.toMillis(minutes);

		StringBuilder sb = new StringBuilder(64);
		sb.append(days);
		sb.append(" Days, ");
		sb.append(hours);
		sb.append(" Hours, ");
		sb.append(minutes);
		sb.append(" Minutes and ");
		sb.append(seconds);
		sb.append(" Seconds");

		return (sb.toString());
	}

	private static String inMegaBytes(double heapMax) {
		return DECIMALS.format(heapMax / (1024 * 1024)) + " MB";
	}

}
