package nl.tudelft.ewi.dea.jaxrs.register;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Value;

@Value
@Setter(AccessLevel.NONE)
public class SignupRequest {
	String email;
}