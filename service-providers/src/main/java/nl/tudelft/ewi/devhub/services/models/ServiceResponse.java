package nl.tudelft.ewi.devhub.services.models;

import lombok.Data;

@Data
public class ServiceResponse {

	private final boolean success;
	private final String message;

}
