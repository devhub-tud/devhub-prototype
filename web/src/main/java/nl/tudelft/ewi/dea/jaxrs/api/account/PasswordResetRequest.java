package nl.tudelft.ewi.dea.jaxrs.api.account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class PasswordResetRequest {

	private long id;
	private String token;
	private String email;
	private String displayName;
	private String password;

}
