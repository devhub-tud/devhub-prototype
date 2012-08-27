package nl.tudelft.ewi.dea.dao;

@SuppressWarnings("serial")
public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException() {
		super("User not found");
	}

	public UserNotFoundException(long id) {
		super("User with id " + id + " wasn't found");
	}
}
