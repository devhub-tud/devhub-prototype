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
import nl.tudelft.jenkins.jobs.Job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class JenkinsService implements ContinuousIntegrationService {

	private static final Logger LOG = LoggerFactory.getLogger(JenkinsService.class);

	private final JenkinsClient jenkinsClient;
	private final URL baseUrl;

	public JenkinsService(Properties props) {
		this(props.getProperty("host"), Integer.parseInt(props.get("port").toString()), props.getProperty("context"), props.getProperty("username"), props.getProperty("password"));
	}

	public JenkinsService(String hostname, int port, String context, String username, String password) {
		checkArgument(isNotEmpty(hostname), "hostname must be non-empty");
		checkArgument(port > 0, "port must be > 0");

		checkArgument(isNotEmpty(username), "username must be non-empty");
		checkArgument(isNotEmpty(password), "password must be non-empty");

		JenkinsClientFactory factory = new JenkinsClientFactory(hostname, port, context, username, password);

		try {
			this.baseUrl = new URL("http", hostname, port, context);
		} catch (MalformedURLException e) {
			throw new DevHubException("Malformed URL", e);
		}

		jenkinsClient = factory.getJenkinsClient();
	}

	@Override
	public void registerUser(nl.tudelft.ewi.dea.model.User user, String plainTextPassword) {
		LOG.trace("Registering user: {}" + user);

		checkNotNull(user, "user must be non-null");

		jenkinsClient.createUser(user.getNetId(), plainTextPassword, user.getEmail(), user.getDisplayName());
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

	private void checkExistenceOfUsers(List<User> users) throws ServiceException {
		for (User user : users) {
			jenkinsClient.retrieveUser(user.getName());
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
