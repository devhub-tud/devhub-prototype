package nl.tudelft.ewi.devhub.services.continuousintegration;

import java.util.List;

import nl.tudelft.ewi.devhub.services.Service;
import nl.tudelft.ewi.devhub.services.ServiceException;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildIdentifier;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildProject;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;

public interface ContinuousIntegrationService extends Service {

	void registerUser(ServiceUser user, String password) throws ServiceException;

	boolean userAlreadyRegistered(ServiceUser user) throws ServiceException;

	/**
	 * @return The URL for the project
	 */
	String createBuildProject(BuildProject project) throws ServiceException;

	void removeBuildProject(BuildIdentifier buildId) throws ServiceException;

	void addMembers(String projectId, List<ServiceUser> users) throws ServiceException;

}
