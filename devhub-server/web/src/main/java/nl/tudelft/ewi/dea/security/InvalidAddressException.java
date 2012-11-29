package nl.tudelft.ewi.dea.security;

/**
 * Is thrown when an Email address is not valid.
 */
@SuppressWarnings("serial")
public class InvalidAddressException extends RuntimeException {

	private static String generateMessage(String address) {
		return address + " is not a valid address";
	}

	/**
	 * @param address The email address.
	 * @param cause The cause.
	 */
	public InvalidAddressException(String address, Throwable cause) {
		super(generateMessage(address), cause);
	}

	/**
	 * @param address The email address.
	 */
	public InvalidAddressException(String address) {
		super(generateMessage(address));
	}

}
