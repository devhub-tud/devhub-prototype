package nl.tudelft.ewi.dea.jaxrs.dashboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.minicom.gitolite.manager.ConfigManager;
import nl.minicom.gitolite.manager.models.Config;
import nl.minicom.gitolite.manager.models.Permission;
import nl.minicom.gitolite.manager.models.Repository;
import nl.minicom.gitolite.manager.models.User;
import nl.tudelft.ewi.dea.jaxrs.utils.Renderer;
import nl.tudelft.jenkins.auth.UserImpl;
import nl.tudelft.jenkins.client.JenkinsClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

@Singleton
@Path("dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

	private static final Logger LOG = LoggerFactory.getLogger(DashboardResource.class);

	private final ConfigManager gitManager;
	private final JenkinsClient jenkinsClient;
	private final Provider<Renderer> renderers;

	@Inject
	public DashboardResource(ConfigManager gitManager, JenkinsClient jenkinsClient, Provider<Renderer> renderers) {
		this.gitManager = gitManager;
		this.jenkinsClient = jenkinsClient;
		this.renderers = renderers;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String servePage() {
		List<Project> invitations = Lists.newArrayList(
				new Project("IN2610 Advanced Algorithms - Group 22")
				);

		List<Project> projects = Lists.newArrayList(
				new Project("IN2010 Algorithms - Group 5"),
				new Project("IN2105 Software Quality & Testing - Group 8"),
				new Project("IN2505 Context project 1 - Group 7")
				);

		return renderers.get()
				.setValue("invitations", invitations)
				.setValue("projects", projects)
				.setValue("scripts", Lists.newArrayList("dashboard.js"))
				.render("dashboard.tpl");
	}

	@GET
	@Path("checkName")
	public Response checkProjectName(@QueryParam("name") String name) {
		if (isValidProjectName(name)) {
			return Response.ok().build();
		}
		return Response.status(Status.CONFLICT).build();
	}

	@POST
	@Path("create")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response provisionNewProject(CreateProjectRequest request) {
		String name = request.getName();
		if (!isValidProjectName(name)) {
			return Response.status(Status.CONFLICT).entity("Project name is not valid!").build();
		}

		Response gitProvisioning = provisionGitRepository(name);
		if (gitProvisioning.getStatus() != Status.OK.getStatusCode()) {
			return gitProvisioning;
		}

		return provisionJenkins(name, "git@dea.hartveld.com:" + name, "M.deJong-2@student.tudelft.nl");
	}

	private Response provisionJenkins(final String projectName, final String gitUrl, final String email) {
		final List<nl.tudelft.jenkins.auth.User> owners = new ArrayList<>();
		owners.add(new UserImpl(email, email));

		try {
			jenkinsClient.createJob(projectName, gitUrl, owners);
		} catch (final Exception e) {
			LOG.warn("Failed to create job", e);
			return Response.serverError().entity("Failed to create job: " + e.getMessage()).build();
		}

		return Response.ok().build();
	}

	private Response provisionGitRepository(String name) {
		Config config = null;
		try {
			config = gitManager.getConfig();
		} catch (/* TODO: Fix this in gitolite-admin */NullPointerException | IOException e) {
			return Response.serverError().entity(new GenericEntity<String>("Currently unable to create git repositories!", String.class)).build();
		}

		if (config.hasRepository(name)) {
			return Response.status(Status.CONFLICT).entity("Repository alreay exists!").build();
		}

		User admin = config.ensureUserExists("git");
		Repository repo = config.createRepository(name);
		repo.setPermission(admin, Permission.ALL);

		try {
			gitManager.applyConfig();
		} catch (IOException e) {
			return Response.serverError().entity("Could not create git repository!").build();
		}

		return Response.ok().build();
	}

	private boolean isValidProjectName(String name) {
		if (name == null || !name.matches("[a-zA-Z0-9]{4,}")) {
			return false;
		}

		try {
			return !gitManager.getConfig().hasRepository(name);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}

	// TODO: Temp class, this should be replaced with something reading from the
	// database...
	public static class Project {
		private final String name;

		public Project(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

}
