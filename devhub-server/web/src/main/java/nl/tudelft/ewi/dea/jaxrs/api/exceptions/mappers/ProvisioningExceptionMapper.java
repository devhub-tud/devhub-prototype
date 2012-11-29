package nl.tudelft.ewi.dea.jaxrs.api.exceptions.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import nl.tudelft.ewi.dea.jaxrs.api.projects.provisioner.ProvisioningException;

@Provider
public class ProvisioningExceptionMapper implements ExceptionMapper<ProvisioningException> {

	@Override
	public Response toResponse(final ProvisioningException exception) {
		return Response.status(Status.CONFLICT)
				.entity(exception.getMessage())
				.build();
	}

}
