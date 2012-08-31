package nl.tudelft.ewi.dea.jaxrs.root;

import java.net.URI;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Singleton
@Path("/")
public class RootResource {

	@GET
	public Response redirectToDashboard() {
		return Response.seeOther(URI.create("/dashboard")).build();
	}

}
