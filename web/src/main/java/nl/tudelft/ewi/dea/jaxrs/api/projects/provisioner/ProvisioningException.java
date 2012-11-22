package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

import nl.tudelft.ewi.dea.DevHubException;

public class ProvisioningException extends DevHubException {

	private static final long serialVersionUID = -896420082420900823L;

	public ProvisioningException(String message) {
		super(message);
	}

	public ProvisioningException(String message, Throwable cause) {
		super(message, cause);
	}

}
