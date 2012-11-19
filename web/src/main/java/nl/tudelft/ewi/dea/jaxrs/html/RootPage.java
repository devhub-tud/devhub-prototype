package nl.tudelft.ewi.dea.jaxrs.html;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("/")
public class RootPage {

	@GET
	public Response redirectToDashboard() {
		return Response.seeOther(URI.create("/dashboard")).build();
	}

}
