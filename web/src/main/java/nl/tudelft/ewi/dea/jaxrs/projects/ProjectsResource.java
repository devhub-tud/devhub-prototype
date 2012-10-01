package nl.tudelft.ewi.dea.jaxrs.projects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
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
import nl.tudelft.jenkins.auth.UserImpl;
import nl.tudelft.jenkins.client.JenkinsClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("projects")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectsResource {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectsResource.class);

	private final ConfigManager gitManager;
	private final JenkinsClient jenkinsClient;

	@Inject
	public ProjectsResource(final ConfigManager gitManager, final JenkinsClient jenkinsClient) {
		this.gitManager = gitManager;
		this.jenkinsClient = jenkinsClient;
	}

	@GET
	@Path("checkName")
	@Transactional
	public Response checkProjectName(@QueryParam("name") final String name) {
		if (!isValidProjectName(name)) {
			return Response.status(Status.CONFLICT).entity("invalid-name").build();
		}
		if (!projectNameIsAvailable(name)) {
			return Response.status(Status.CONFLICT).entity("already-taken").build();
		}
		return Response.ok().build();
	}

	@POST
	@Path("create")
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response provisionNewProject(final CreateProjectRequest request) {
		final String name = request.getName();
		if (!isValidProjectName(name) || !projectNameIsAvailable(name)) {
			return Response.status(Status.CONFLICT).entity("Project name is not valid!").build();
		}

		final Response gitProvisioning = provisionGitRepository(name);
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
			return Response.serverError().entity("Failed to create Jenkins job").build();
		}

		return Response.ok().build();
	}

	private Response provisionGitRepository(final String name) {
		Config config = null;
		try {
			config = gitManager.getConfig();
		} catch (/* TODO: Fix this in gitolite-admin */NullPointerException | IOException e) {
			return Response.serverError().entity(new GenericEntity<String>("Currently unable to create git repositories!", String.class)).build();
		}

		if (config.hasRepository(name)) {
			return Response.status(Status.CONFLICT).entity("Repository alreay exists!").build();
		}

		final User admin = config.ensureUserExists("git");
		final Repository repo = config.createRepository(name);
		repo.setPermission(admin, Permission.ALL);

		try {
			gitManager.applyConfig();
		} catch (final IOException e) {
			return Response.serverError().entity("Could not create git repository!").build();
		}

		return Response.ok().build();
	}

	private boolean isValidProjectName(final String name) {
		return name != null && name.matches("[a-zA-Z0-9]{4,}");
	}

	private boolean projectNameIsAvailable(final String name) {
		try {
			return !gitManager.getConfig().hasRepository(name);
		} catch (final IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}

}
