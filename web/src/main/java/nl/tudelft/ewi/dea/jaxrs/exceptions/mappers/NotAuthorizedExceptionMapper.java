package nl.tudelft.ewi.dea.jaxrs.exceptions.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import nl.tudelft.ewi.dea.jaxrs.exceptions.NotAuthorizedException;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {
	@Override
	public Response toResponse(NotAuthorizedException ex) {
		return Response.status(Status.UNAUTHORIZED).build();
	}
}