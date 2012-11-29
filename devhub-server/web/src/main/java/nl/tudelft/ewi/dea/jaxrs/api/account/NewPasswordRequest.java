package nl.tudelft.ewi.dea.jaxrs.api.account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class NewPasswordRequest {

	private String password;

}
