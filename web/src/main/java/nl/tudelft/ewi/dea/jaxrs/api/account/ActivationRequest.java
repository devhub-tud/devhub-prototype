package nl.tudelft.ewi.dea.jaxrs.api.account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class ActivationRequest {

	private final String email;
	private final String password;
	private final String displayName;
	private final String netId;
	private final int studentNumber;

	public ActivationRequest() {
		this.email = null;
		this.password = null;
		this.displayName = null;
		this.netId = null;
		this.studentNumber = 0;
	}
}
