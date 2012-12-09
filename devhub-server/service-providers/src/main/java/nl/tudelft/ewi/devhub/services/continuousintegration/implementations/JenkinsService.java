package nl.tudelft.ewi.devhub.services.continuousintegration.implementations;

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
import nl.tudelft.jenkins.client.JenkinsClientImpl;

import org.jclouds.Context;
import org.jclouds.ContextBuilder;
import org.jclouds.jenkins.v1.JenkinsApi;
import org.jclouds.jenkins.v1.JenkinsAsyncApi;
import org.jclouds.rest.RestContext;

import com.google.common.collect.Lists;

public class JenkinsService implements ContinuousIntegrationService {

	private final JenkinsClient jenkinsClient;

	public JenkinsService(Properties properties) {
		this(properties.getProperty("url"));
	}

	@SuppressWarnings("unchecked")
	public JenkinsService(String jenkinsUri) {
		final Context context = ContextBuilder.newBuilder("jenkins").endpoint(jenkinsUri).build();
		final RestContext<JenkinsApi, JenkinsAsyncApi> jenkinsRestContext = (RestContext<JenkinsApi, JenkinsAsyncApi>) context;
		jenkinsClient = new JenkinsClientImpl(jenkinsRestContext);
	}

	@Override
	public void createBuildProject(BuildProject project) throws ServiceException {
		List<User> users = Lists.newArrayList();
		for (ServiceUser member : project.getMembers()) {
			users.add(new UserImpl(member.getIdentifier(), member.getEmail()));
		}

		try {
			jenkinsClient.createJob(project.getName(), project.getSourceCodeUrl(), users);
		} catch (Throwable e) {
			throw new ServiceException("Could not create the defined Jenkins job!");
		}
	}

	@Override
	public void removeBuildProject(BuildIdentifier buildId) throws ServiceException {
		try {
			jenkinsClient.deleteJob(jenkinsClient.retrieveJob(buildId.getName()));
		} catch (Throwable e) {
			throw new ServiceException("Could not remove the specified Jenkins job!");
		}
	}

	@Override
	public String getName() {
		return "Jenkins";
	}

}
