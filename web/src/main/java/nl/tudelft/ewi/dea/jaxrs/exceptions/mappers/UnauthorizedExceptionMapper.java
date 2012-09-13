package nl.tudelft.ewi.dea.jaxrs.exceptions.mappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

	private static final Logger LOG = LoggerFactory.getLogger(UnauthorizedExceptionMapper.class);

	@Override
	public Response toResponse(final UnauthorizedException exception) {

		LOG.debug("Mapping exception to HTTP 401 Unauthorized: {}", exception.getMessage(), exception);

		return Response.status(Status.UNAUTHORIZED)
				.entity("Current user is not authorized to access this page")
				.build();

	}

}
