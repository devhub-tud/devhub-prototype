package nl.tudelft.ewi.dea.jaxrs.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
@Path("exception")
public class ExceptionAASResource {

	@GET
	public void throwException() {
		throw new RuntimeException("Your Exception-As-A-Service is served!");
	}

}
