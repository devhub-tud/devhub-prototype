package nl.tudelft.ewi.devhub.services.continuousintegration;

import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.devhub.services.Service;
import nl.tudelft.ewi.devhub.services.ServiceException;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildIdentifier;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildProject;

public interface ContinuousIntegrationService extends Service {

	void registerUser(User user, String plainTextPassword) throws ServiceException;

	void createBuildProject(BuildProject project) throws ServiceException;

	void removeBuildProject(BuildIdentifier buildId) throws ServiceException;

}
