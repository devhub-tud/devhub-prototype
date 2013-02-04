package nl.tudelft.ewi.dea.jaxrs.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("api/exception")
public class ExceptionAASResource {

	@GET
	public Response throwException() {
		throw new RuntimeException("Your Exception-As-A-Service is served!");
	}

}
