package nl.tudelft.ewi.dea.jaxrs.html;

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
public class RootPage {

	private final BuildInfo version;

	@Inject
	RootPage(BuildInfo version) {
		this.version = version;
	}

	@GET
	public Response redirectToDashboard() {
		return Response.seeOther(URI.create("/dashboard")).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("version")
	public BuildInfo version() {
		return version;
	}

}
