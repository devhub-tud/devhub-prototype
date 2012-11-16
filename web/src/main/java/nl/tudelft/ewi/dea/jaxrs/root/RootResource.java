package nl.tudelft.ewi.dea.jaxrs.root;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.tudelft.ewi.dea.BuildInfo;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("/")
public class RootResource {

	private BuildInfo version;

	@Inject
	RootResource(BuildInfo version) {
		this.version = version;
	}

	@GET
	public Response redirectToDashboard() {
		return Response.seeOther(URI.create("/dashboard")).build();
	}

	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
	public BuildInfo version() {
		return version;
	}

}
