package nl.tudelft.ewi.dea.jaxrs.register;

import lombok.experimental.Value;

@Value
public class CheckEmailRequest {
	String email;
}