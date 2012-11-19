package nl.tudelft.ewi.dea.jaxrs.api.register;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class SignupRequest {

	private final String email;

	public SignupRequest() {
		email = null;
	}

}