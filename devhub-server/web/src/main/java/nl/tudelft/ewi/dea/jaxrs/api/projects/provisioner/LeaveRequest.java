package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

import lombok.Data;
import nl.tudelft.ewi.dea.model.User;

@Data
public class LeaveRequest {

	private final User user;
	private final long projectId;

}
