package nl.tudelft.ewi.dea.jaxrs.exceptions.mappers;

import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class NoResultExceptionMapper implements ExceptionMapper<NoResultException> {

	private static final Logger LOG = LoggerFactory.getLogger(NoResultExceptionMapper.class);

	@Override
	public Response toResponse(final NoResultException exception) {

		LOG.debug("Mapping exception to HTTP 404 Not found: {}", exception.getMessage(), exception);

		return Response.status(Status.NOT_FOUND)
				.entity(exception.getMessage())
				.build();

	}

}
