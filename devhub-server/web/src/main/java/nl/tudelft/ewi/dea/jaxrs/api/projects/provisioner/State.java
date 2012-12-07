package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

import lombok.Data;

@Data
public class State {
	private final boolean finished;
	private final boolean failures;
	private final String message;
}