package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

import java.util.List;

import lombok.Data;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;

@Data
public class ProvisioningRequest {

	private final User creator;
	private final List<String> invited;
	private final long courseId;

	private final VersionControlService versionControlService;
	private final ContinuousIntegrationService continuousIntegrationService;

}
