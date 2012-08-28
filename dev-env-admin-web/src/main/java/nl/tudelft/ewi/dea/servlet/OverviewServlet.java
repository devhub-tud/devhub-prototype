package nl.tudelft.ewi.dea.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.minicom.gitolite.manager.ConfigManager;
import nl.minicom.gitolite.manager.models.Config;
import nl.minicom.gitolite.manager.models.Permission;
import nl.minicom.gitolite.manager.models.Repository;
import nl.minicom.gitolite.manager.models.User;
import nl.tudelft.ewi.dea.servlet.util.Get;
import nl.tudelft.ewi.dea.servlet.util.Post;
import nl.tudelft.ewi.dea.servlet.util.Response;
import nl.tudelft.ewi.dea.servlet.util.Servlet;
import nl.tudelft.jenkins.auth.UserImpl;
import nl.tudelft.jenkins.client.JenkinsClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

@Singleton
@SuppressWarnings("serial")
public class OverviewServlet extends Servlet {

	private static final Logger LOG = LoggerFactory.getLogger(OverviewServlet.class);

	private final ConfigManager gitoliteManager;
	private final JenkinsClient jenkinsClient;

	@Inject
	public OverviewServlet(ConfigManager gitoliteManager, JenkinsClient jenkinsClient) {
		this.gitoliteManager = gitoliteManager;
		this.jenkinsClient = jenkinsClient;
	}

	@Override
	public void onGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Project> invitations = Lists.newArrayList(
				new Project("IN2610 Advanced Algorithms - Group 22")
		);

		List<Project> projects = Lists.newArrayList(
				new Project("IN2010 Algorithms - Group 5"),
				new Project("IN2105 Software Quality & Testing - Group 8"),
				new Project("IN2505 Context project 1 - Group 7")
		);

		getRenderer()
			.setValue("invitations", invitations)
			.setValue("projects", projects)
			.setValue("scripts", Lists.newArrayList("overview.js"))
			.render("index.tpl");
	}

	@Get
	public Response checkProjectName(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("name");
		return new Response(isValidProjectName(name));
	}

	@Post
	public Response provisionNewProject(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("name");
		if (!isValidProjectName(name)) {
			return new Response(false, "Project name is not valid!");
		}

		Response gitProvisioning = provisionGitRepository(name);
		if (!gitProvisioning.isOk()) {
			return gitProvisioning;
		}

		return provisionJenkins(name, "", "");
	}

	private Response provisionJenkins(final String projectName, final String gitUrl, final String email) {

		final List<nl.tudelft.jenkins.auth.User> owners = new ArrayList<>();
		owners.add(new UserImpl(email, email));

		try {
			jenkinsClient.createJob(projectName, gitUrl, owners);
		} catch (final Exception e) {
			LOG.warn("Failed to create job", e);
			return new Response(false, "Failed to create job: " + e.getMessage());
		}

		return new Response(true, "Job created successfully");

	}

	private Response provisionGitRepository(String name) {
		Config config = null;
		try {
			config = gitoliteManager.getConfig();
		}
		catch (IOException e) {
			return new Response(false, "Currently unable to create git repositories!");
		}

		if (config.hasRepository(name)) {
			return new Response(false, "Repository alreay exists!");
		}
		
		User admin = config.ensureUserExists("git");
		Repository repo = config.createRepository(name);
		repo.setPermission(admin, Permission.ALL);

		try {
			gitoliteManager.applyConfig();
		} 
		catch (IOException e) {
			return new Response(false, "Could not create git repository!");
		}

		return new Response(true);
	}

	private boolean isValidProjectName(String name) {
		if (name == null || !name.matches("[a-zA-Z0-9]{4,}")) {
			return false;
		}
		try {
			return !gitoliteManager.getConfig().hasRepository(name);
		}
		catch (IOException e) {
			// Ignore this
		}
		return false;
	}

	public class Project {
		private final String name;

		public Project(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

}
