package nl.tudelft.ewi.dea.jaxrs.projects.provisioner;

import nl.tudelft.ewi.dea.DevHubException;

public class ProvisioningException extends DevHubException {

	private static final long serialVersionUID = -896420082420900823L;

	public ProvisioningException(String message) {
		super(message);
	}

}
