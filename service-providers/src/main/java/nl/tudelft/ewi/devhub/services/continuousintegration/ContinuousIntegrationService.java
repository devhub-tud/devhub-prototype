package nl.tudelft.ewi.devhub.services.continuousintegration;

import java.util.concurrent.Future;

import nl.tudelft.ewi.devhub.services.Service;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildIdentifier;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildProject;
import nl.tudelft.ewi.devhub.services.models.ServiceResponse;

public interface ContinuousIntegrationService extends Service {

	Future<ServiceResponse> createBuildProject(BuildProject project);

	Future<ServiceResponse> removeBuildProject(BuildIdentifier buildId);

}
