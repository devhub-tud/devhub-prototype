package nl.tudelft.ewi.dea.jaxrs.api.projects;

import nl.tudelft.ewi.dea.DevHubException;
import nl.tudelft.ewi.dea.model.Project;

/**
 * Is thrown when an invitation for a {@link Project} goes bad.
 * 
 */
@SuppressWarnings("serial")
public class InviteException extends DevHubException {

	public InviteException(String message) {
		super(message);
	}

}
