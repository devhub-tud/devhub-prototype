package nl.tudelft.ewi.dea.mail;

/**
 * Can occur when something unexpected happens in the mail module.
 */
@SuppressWarnings("serial")
public class MailException extends RuntimeException {

	/**
	 * @see RuntimeException#RuntimeException(String, Throwable)
	 */
	public MailException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see RuntimeException#RuntimeException(String)
	 */
	public MailException(String message) {
		super(message);
	}

	/**
	 * @see RuntimeException#RuntimeException(Throwable)
	 */
	public MailException(Throwable cause) {
		super(cause);
	}

}
