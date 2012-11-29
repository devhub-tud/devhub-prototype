package nl.tudelft.ewi.dea;

import java.io.IOException;

/**
 * Is thrown when something is wrong with the DevHub configuration.
 * 
 */
@SuppressWarnings("serial")
public class ConfigurationException extends DevHubException {

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(String message, IOException exception) {
		super(message, exception);
	}

}
