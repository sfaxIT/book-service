package de.sfaxit.web.exception;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<MismatchedInputException> {
	
	@Override
	public Response toResponse(final MismatchedInputException e) {
		return Response.status(BAD_REQUEST)
		               .entity(e.getMessage())
		               .build();
	}

}
