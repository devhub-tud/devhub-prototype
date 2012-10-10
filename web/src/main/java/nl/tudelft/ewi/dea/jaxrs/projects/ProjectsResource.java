package nl.tudelft.ewi.dea.jaxrs.projects;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.jaxrs.projects.provisioner.Provisioner;
import nl.tudelft.ewi.dea.jaxrs.projects.provisioner.Provisioner.State;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("projects")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectsResource {

	private final Provisioner provisioner;
	private final SecurityProvider securityProvider;

	@Inject
	public ProjectsResource(SecurityProvider securityProvider, Provisioner provisioner) {
		this.securityProvider = securityProvider;
		this.provisioner = provisioner;
	}

	@POST
	@Transactional
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createNewProject(CourseProjectRequest request) {
		User currentUser = securityProvider.getUser();
		long projectId = provisioner.provision(request, currentUser);
		return Response.ok().entity(projectId).build();
	}

	@GET
	@Path("provisioning/{projectId}")
	@Transactional
	public Response checkProvisioningState(@PathParam("projectId") long projectId) {
		State state = provisioner.getState(projectId);
		if (state.isFailures()) {
			return Response.status(Status.CONFLICT).entity(state).build();
		}
		return Response.ok().entity(state).build();
	}

}
