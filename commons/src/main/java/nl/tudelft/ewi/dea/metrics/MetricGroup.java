package nl.tudelft.ewi.dea.metrics;

import com.yammer.metrics.core.MetricName;

public enum MetricGroup {

	WEB("Web"),
	APP("Application");

	private final String groupName;

	MetricGroup(String name) {
		this.groupName = name;
	}

	public MetricName newName(String name) {
		return new MetricName(this.groupName, "", name);
	}

	@Override
	public String toString() {
		return groupName;
	}

}
