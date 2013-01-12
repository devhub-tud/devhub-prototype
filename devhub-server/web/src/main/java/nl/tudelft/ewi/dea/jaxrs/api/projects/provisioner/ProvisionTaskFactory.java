package nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner;

import java.util.Set;

import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;

public interface ProvisionTaskFactory {

	ProvisionTask create(Provisioner provisioner, VersionControlService versioningService, ContinuousIntegrationService buildService, long id, Set<String> invited);

}
