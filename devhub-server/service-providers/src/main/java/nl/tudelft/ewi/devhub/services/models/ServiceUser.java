package nl.tudelft.ewi.devhub.services.models;

import lombok.Data;

@Data
public class ServiceUser {

	private final String identifier;
	private final String fullName;
	private final String email;

	@Override
	public String toString() {
		return fullName;
	}

}
