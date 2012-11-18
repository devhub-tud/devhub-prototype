package nl.tudelft.ewi.devhub.services.models;

import lombok.experimental.Value;

@Value
public class ServiceResponse {

	private final boolean success;
	private final String message;

}
