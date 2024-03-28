package de.sfaxit.web;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import de.sfaxit.model.dto.LoginDTO;
import de.sfaxit.model.dto.PagedCollectionResponseDTO;
import de.sfaxit.model.dto.SearchResultHolderDTO;
import de.sfaxit.model.dto.UserDTO;
import de.sfaxit.model.dto.enums.UserRole;
import de.sfaxit.model.entity.Author;
import de.sfaxit.service.AuthorService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestQuery;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class UserResource {
	
	@Inject
	AuthorService service;
	
	@Inject
	JsonWebToken accessToken;
	
	@POST
	@Path("/register")
	@Operation(description = "Register a user with required data", summary = "Register a user")
	@APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class)))
	@APIResponse(responseCode = "400", description = "In case the provided username or password is empty")
	@APIResponse(responseCode = "409", description = "In case the given username conflicts with an existing one")
	@APIResponse(responseCode = "500", description = "Internal Server Error")
	public Response register(@RestQuery final String username,
	                         @RestQuery final String password,
	                         @RestQuery @DefaultValue(EMPTY) final String role) {
		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity("Request must contain the mandatory credentials username and password")
			               .build();
		}
		
		if (this.service.authorExists(username)) {
			return Response.status(Response.Status.CONFLICT)
			               .entity("Author already exists with username {} " + username)
			               .build();
		}
		
		final Author userDTO = this.service.registerUser(username, password, UserRole.valueOf(role));
		if (userDTO != null) {
			return Response.ok(userDTO)
			               .build();
		}
		
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		               .entity("Internal Server Error")
		               .build();
	}
	
	@GET
	@Path("/login")
	@Operation(description = "Retrieve the json web token for login", summary = "Retrieve the jwt")
	@APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginDTO.class)))
	@APIResponse(responseCode = "400", description = "In case the provided username or password is empty or password is wrong")
	@APIResponse(responseCode = "404", description = "In case the requested entity is unknown yet")
	@APIResponse(responseCode = "500", description = "Internal Server Error")
	public Response login(@RestQuery final String username, @RestQuery final String password) {
		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity("Request must contain the mandatory credentials username and password")
			               .build();
		}
		
		if (!this.service.authorExists(username)) {
			return Response.status(Response.Status.NOT_FOUND)
			               .entity("Author not found with username {}" + username)
			               .build();
		}
		
		final Author registeredUser = this.service.findByUsername(username);
		if (!this.service.isValidPassword(registeredUser, password)) {
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity("Wrong password")
			               .build();
		}
		
		final LoginDTO jwtDto = this.service.getUserToken(registeredUser);
		if (jwtDto != null) {
			return Response.ok(jwtDto)
			               .build();
		}
		
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		               .entity("Internal Server Error")
		               .build();
	}
	
	@GET
	@Path("/all")
	@RolesAllowed({"ADMIN"})
	@Operation(operationId = "get", description = "Retrieve registered users requested by authenticated admin", summary = "Retrieve all users")
	@APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class)))
	@APIResponse(responseCode = "401", description = "In case of unauthorized access attempts")
	@APIResponse(responseCode = "500", description = "Internal Server Error")
	public Response readAllUsers(@RestQuery @Min(2) final Integer size) {
		final Author admin = this.service.findByUsername(accessToken.getSubject());
		
		if (admin == null) {
			return Response.status(Response.Status.NOT_FOUND)
			               .entity("admin not found in database")
			               .build();
		}
		
		final SearchResultHolderDTO allAuthors = this.service.findAllAuthorsByPage(size);
		
		if (allAuthors == null) {
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity("Author library is empty")
			               .build();
		}
		
		return Response.status(Response.Status.OK)
		               .entity(PagedCollectionResponseDTO.of(allAuthors.getUsers(), allAuthors.getPageCount(), allAuthors.getCurrentPage(), allAuthors.getTotalCount()))
		               .build();
	}
	
}
