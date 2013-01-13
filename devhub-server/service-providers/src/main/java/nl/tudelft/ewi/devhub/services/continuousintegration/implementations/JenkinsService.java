package nl.tudelft.ewi.devhub.services.continuousintegration.implementations;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import nl.tudelft.ewi.dea.DevHubException;
import nl.tudelft.ewi.devhub.services.ServiceException;
import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildIdentifier;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildProject;
import nl.tudelft.ewi.devhub.services.models.ServiceUser;
import nl.tudelft.jenkins.auth.User;
import nl.tudelft.jenkins.auth.UserImpl;
import nl.tudelft.jenkins.client.JenkinsClient;
import nl.tudelft.jenkins.client.JenkinsClientFactory;
import nl.tudelft.jenkins.client.exceptions.JenkinsException;
import nl.tudelft.jenkins.jobs.Job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class JenkinsService implements ContinuousIntegrationService {

	private static final Logger LOG = LoggerFactory.getLogger(JenkinsService.class);

	private final JenkinsClient jenkinsClient;
	private final URL baseUrl;

	public static JenkinsService fromProperties(Properties props) {
		URL url;
		try {
			url = new URL(props.getProperty("url"));
		} catch (MalformedURLException e) {
			throw new DevHubException("Invalid Jenkins URL in " + props);
		}
		return new JenkinsService(url, props.getProperty("username"), props.getProperty("password"));
	}

	public JenkinsService(URL url, String username, String password) {
		checkNotNull(url);
		checkArgument(isNotEmpty(username), "username must be non-empty");
		checkArgument(isNotEmpty(password), "password must be non-empty");

		this.baseUrl = url;
		JenkinsClientFactory factory = new JenkinsClientFactory(baseUrl, username, password);

		jenkinsClient = factory.getJenkinsClient();
	}

	@Override
	public void registerUser(ServiceUser user, String plainTextPassword) {
		LOG.trace("Registering user: {}" + user);

		checkNotNull(user, "user must be non-null");

		jenkinsClient.createUser(user.getIdentifier(), plainTextPassword, user.getEmail(), user.getFullName());
	}

	@Override
	public URL createBuildProject(BuildProject project) throws ServiceException {
		List<User> users = Lists.newArrayList();
		for (ServiceUser member : project.getMembers()) {
			users.add(new UserImpl(member.getIdentifier(), member.getEmail()));
		}

		try {
			checkExistenceOfUsers(users);

			Job job = jenkinsClient.createJob(project.getName(), project.getSourceCodeUrl(), users);
			return new URL(baseUrl.toExternalForm() + "/job/" + job.getName());
		} catch (Throwable e) {
			throw new ServiceException("Could not create the defined Jenkins job!", e);
		}
	}

	@Override
	public boolean userAlreadyRegistered(ServiceUser user) throws ServiceException {
		try {
			User jenkinsUser = new UserImpl(user.getIdentifier(), user.getEmail());
			checkExistenceOfUsers(Lists.newArrayList(jenkinsUser));
			return true;
		} catch (JenkinsException e) {
			return false;
		}
	}

	private void checkExistenceOfUsers(List<User> users) throws ServiceException {
		for (User user : users) {
			jenkinsClient.retrieveUser(user.getName());
		}
	}

	@Override
	public void addMembers(String projectId, List<ServiceUser> users) throws ServiceException {
		try {
			Job job = jenkinsClient.retrieveJob(projectId);
			List<User> jenkinsUsers = job.getUsers();
			for (ServiceUser user : users) {
				User jenkinsUser = new UserImpl(user.getIdentifier(), user.getEmail());
				if (!jenkinsUsers.contains(jenkinsUser)) {
					jenkinsUsers.add(jenkinsUser);
				}
			}
			jenkinsClient.updateJob(job);
		} catch (Throwable e) {
			throw new ServiceException("Could not add the following users to project: " + projectId + " - " + Joiner.on(", ").join(users), e);
		}
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
