package nl.tudelft.ewi.dea.jaxrs.api.projects;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner.Provisioner;
import nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner.ProvisioningRequest;
import nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner.State;
import nl.tudelft.ewi.dea.model.User;
import nl.tudelft.ewi.dea.security.SecurityProvider;
import nl.tudelft.ewi.devhub.services.ServiceProvider;
import nl.tudelft.ewi.devhub.services.continuousintegration.ContinuousIntegrationService;
import nl.tudelft.ewi.devhub.services.versioncontrol.VersionControlService;

import com.google.common.collect.Lists;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/projects")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProjectsResource {

	private final Provisioner provisioner;
	private final SecurityProvider securityProvider;
	private final ServiceProvider serviceProvider;

	@Inject
	public ProjectsResource(SecurityProvider securityProvider, ServiceProvider serviceProvider, Provisioner provisioner) {
		this.securityProvider = securityProvider;
		this.serviceProvider = serviceProvider;
		this.provisioner = provisioner;
	}

	@POST
	public Response createNewProject(CourseProjectRequest request) {
		User currentUser = securityProvider.getUser();
		VersionControlService versioningService = serviceProvider.getVersionControlService(request.getVersionControlService());
		ContinuousIntegrationService buildService = serviceProvider.getContinuousIntegrationService(request.getContinuousIntegrationService());
		provisioner.provision(new ProvisioningRequest(currentUser, Lists.newArrayList(request.getInvites()), request.getCourse(), versioningService, buildService));
		return Response.ok().build();
	}

	@GET
	@Path("provisioning")
	public Response checkProvisioningState() {
		User currentUser = securityProvider.getUser();
		State state = provisioner.getState(currentUser.getNetId());
		if (state.isFailures()) {
			return Response.status(Status.CONFLICT).entity(state).build();
		}
		return Response.ok().entity(state).build();
	}

}
