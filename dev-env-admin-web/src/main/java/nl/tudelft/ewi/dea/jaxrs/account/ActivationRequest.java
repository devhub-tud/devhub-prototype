package nl.tudelft.ewi.dea.jaxrs.account;

import lombok.experimental.Value;

@Value
public class ActivationRequest {
	private final String email;
	private final String password;
	private final String displayName;
	private final String netId;
	private final int studentNumber;
}
