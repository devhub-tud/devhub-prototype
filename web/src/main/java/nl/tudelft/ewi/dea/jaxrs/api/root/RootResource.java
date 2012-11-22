package nl.tudelft.ewi.dea.jaxrs.api.root;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.tudelft.ewi.dea.BuildInfo;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api")
@Produces(MediaType.APPLICATION_JSON)
public class RootResource {

	private final BuildInfo version;

	@Inject
	RootResource(BuildInfo version) {
		this.version = version;
	}

	@GET
	@Path("version")
	public BuildInfo version() {
		return version;
	}

}
