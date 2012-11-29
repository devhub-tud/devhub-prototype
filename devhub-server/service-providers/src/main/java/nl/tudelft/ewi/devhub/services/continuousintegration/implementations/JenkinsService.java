package nl.tudelft.ewi.devhub.services.continuousintegration.implementations;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildIdentifier;
import nl.tudelft.ewi.devhub.services.continuousintegration.models.BuildProject;
import nl.tudelft.ewi.devhub.services.models.ServiceResponse;
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
	public Future<ServiceResponse> createBuildProject(final BuildProject project) {
		return execute(new Callable<ServiceResponse>() {
			@Override
			public ServiceResponse call() throws Exception {
				List<User> users = Lists.newArrayList();
				for (ServiceUser member : project.getMembers()) {
					users.add(new UserImpl(member.getIdentifier(), member.getEmail()));
				}

				try {
					jenkinsClient.createJob(project.getName(), project.getSourceCodeUrl(), users);
					return new ServiceResponse(true, "Successfully created continuous integration job!");
				} catch (Throwable e) {
					return new ServiceResponse(false, "Failed to create continuous integration job!");
				}
			}
		});
	}

	@Override
	public Future<ServiceResponse> removeBuildProject(final BuildIdentifier buildId) {
		return execute(new Callable<ServiceResponse>() {
			@Override
			public ServiceResponse call() throws Exception {
				try {
					jenkinsClient.deleteJob(jenkinsClient.retrieveJob(buildId.getName()));
					return new ServiceResponse(true, "Successfully removed continuous integration job!");
				} catch (Throwable e) {
					return new ServiceResponse(false, "Failed to remove continuous integration job!");
				}
			}
		});
	}

	private Future<ServiceResponse> execute(Callable<ServiceResponse> callable) {
		FutureTask<ServiceResponse> task = new FutureTask<ServiceResponse>(callable);
		task.run();
		return task;
	}

	@Override
	public String getName() {
		return "Jenkins";
	}

}
