package nl.tudelft.ewi.devhub.services.continuousintegration.implementations;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.List;
import java.util.Properties;

import nl.tudelft.ewi.devhub.services.ServiceException;
import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildIdentifier;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildProject;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.jenkins.auth.User;
import nl.tudelft.jenkins.auth.UserImpl;
import nl.tudelft.jenkins.client.JenkinsClient;
import nl.tudelft.jenkins.client.JenkinsClientFactory;
import nl.tudelft.jenkins.client.exceptions.NoSuchUserException;

import com.google.common.collect.Lists;

public class JenkinsService implements ContinuousIntegrationService {

	private final JenkinsClient jenkinsClient;

	public JenkinsService(Properties props) {
		this(props.getProperty("host"), Integer.parseInt(props.get("port").toString()), props.getProperty("context"), props.getProperty("username"), props.getProperty("password"));
	}

	public JenkinsService(String hostname, int port, String context, String username, String password) {

		checkArgument(isNotEmpty(hostname), "hostname must be non-empty");
		checkArgument(port > 0, "port must be > 0");

		checkArgument(isNotEmpty(username), "username must be non-empty");
		checkArgument(isNotEmpty(password), "password must be non-empty");

		JenkinsClientFactory factory = new JenkinsClientFactory(hostname, port, context, username, password);
		jenkinsClient = factory.getJenkinsClient();
	}

	@Override
	public void createBuildProject(BuildProject project) throws ServiceException {
		List<User> users = Lists.newArrayList();
		for (ServiceUser member : project.getMembers()) {
			users.add(new UserImpl(member.getIdentifier(), member.getEmail()));
		}

		try {
			for (User user : users) {
				if (userDoesNotExist(user)) {
					jenkinsClient.createUser(user.getName(), "1", user.getEmail(), "x");
				}
			}

			jenkinsClient.createJob(project.getName(), project.getSourceCodeUrl(), users);
		} catch (Throwable e) {
			throw new ServiceException("Could not create the defined Jenkins job!", e);
		}

	}

	private boolean userDoesNotExist(User user) {
		try {
			jenkinsClient.retrieveUser(user.getName());
		} catch (NoSuchUserException e) {
			return true;
		}

		return false;
	}

	@Override
	public void removeBuildProject(BuildIdentifier buildId) throws ServiceException {
		try {
			jenkinsClient.deleteJob(jenkinsClient.retrieveJob(buildId.getName()));
		} catch (Throwable e) {
			throw new ServiceException("Could not remove the specified Jenkins job!", e);
		}
	}

	@Override
	public String getName() {
		return "Jenkins";
	}

}
