package nl.tudelft.ewi.dea;

/**
 * Get's thrown when an unkown exception occurs.
 * 
 */
@SuppressWarnings("serial")
public class DevHubException extends RuntimeException {

	public DevHubException() {
		super();
	}

	public DevHubException(String message, Throwable cause) {
		super(message, cause);
	}

	public DevHubException(String message) {
		super(message);
	}

	public DevHubException(Throwable cause) {
		super(cause);
	}

}
