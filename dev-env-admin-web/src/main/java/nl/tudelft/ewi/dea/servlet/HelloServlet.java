package nl.tudelft.ewi.dea.servlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/api/hello")
public class HelloServlet {

	@GET
	@Produces("text/plain")
	public String get(@QueryParam("x") String x) {
		return "Howdy Guice. " + ". Injected query parameter " + x;
	}
}
