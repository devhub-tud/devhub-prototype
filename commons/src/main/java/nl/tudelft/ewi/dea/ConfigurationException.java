package nl.tudelft.ewi.dea;

/**
 * Is thrown when something is wrong with the DevHub configuration.
 * 
 */
@SuppressWarnings("serial")
public class ConfigurationException extends DevHubException {

	public ConfigurationException(String message) {
		super(message);
	}

}
