package de.sfaxit.web.exception;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<WebApplicationException> {
	
	@Override
	public Response toResponse(WebApplicationException e) {
		// Set the JSR-303 error into JSON format.
		return Response.status(BAD_REQUEST)
		               .entity(e.getMessage())
		               .build();
	}

}
