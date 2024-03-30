package de.sfaxit.web;

import de.sfaxit.model.dto.LoginDTO;
import de.sfaxit.model.dto.PagedCollectionResponseDTO;
import de.sfaxit.model.dto.SearchResultHolderDTO;
import de.sfaxit.model.dto.SubscriberDTO;
import de.sfaxit.model.entity.Subscriber;
import de.sfaxit.service.SubscriberService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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

@Path("/subscriber")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class SubscriberResource {
	
	@Inject
	SubscriberService service;
	
	@Inject
	JsonWebToken accessToken;
	
	@POST
	@Path("/register")
	@Operation(description = "Register a subscriber with required data", summary = "Register a subscriber")
	@APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriberDTO.class)))
	@APIResponse(responseCode = "400", description = "In case the provided username or password is empty")
	@APIResponse(responseCode = "409", description = "In case the given username conflicts with an existing one")
	@APIResponse(responseCode = "500", description = "Internal Server Error")
	public Response register(@Valid @NotNull final SubscriberDTO dto) {
		final String username = dto.getUsername();
		if (this.service.subscriberExists(username)) {
			return Response.status(Response.Status.CONFLICT)
			               .entity("Subscriber already exists with username {} " + username)
			               .build();
		}
		
		final Subscriber subscriberDTO = this.service.registerSubscriber(dto);
		if (subscriberDTO != null) {
			return Response.ok(subscriberDTO)
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
	public Response login(@RestQuery @NotBlank final String username, @RestQuery @NotBlank final String password) {
		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity("Request must contain the mandatory credentials username and password")
			               .build();
		}
		
		if (!this.service.subscriberExists(username)) {
			return Response.status(Response.Status.NOT_FOUND)
			               .entity("Subscriber not found with username {}" + username)
			               .build();
		}
		
		final Subscriber registeredSubscriber = this.service.findBySubscriberName(username);
		if (!this.service.isValidPassword(registeredSubscriber, password)) {
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity("Wrong password")
			               .build();
		}
		
		final LoginDTO jwtDto = this.service.getSubscriberToken(registeredSubscriber);
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
	@APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriberDTO.class)))
	@APIResponse(responseCode = "401", description = "In case of unauthorized access attempts")
	@APIResponse(responseCode = "403", description = "In case of forbidden access attempts")
	@APIResponse(responseCode = "500", description = "Internal Server Error")
	public Response readAllUsers(@RestQuery @Min(2) final Integer size) {
		final Subscriber admin = this.service.findBySubscriberName(accessToken.getSubject());
		
		if (admin == null) {
			return Response.status(Response.Status.NOT_FOUND)
			               .entity("admin not found in database")
			               .build();
		}
		
		final SearchResultHolderDTO allUsers = this.service.findAllSubscribersByPage(size);
		
		if (allUsers == null) {
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity("Subscriber library is empty")
			               .build();
		}
		
		return Response.status(Response.Status.OK)
		               .entity(PagedCollectionResponseDTO.of(allUsers.getUsers(), allUsers.getPageCount(), allUsers.getCurrentPage(), allUsers.getTotalCount()))
		               .build();
	}
	
	@PUT
	@Path("/ban")
	@RolesAllowed({"ADMIN"})
	@Operation(operationId = "put", description = "Ban registered users requested by authenticated admin", summary = "Ban user")
	@APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubscriberDTO.class)))
	@APIResponse(responseCode = "401", description = "In case of unauthorized access attempts")
	@APIResponse(responseCode = "403", description = "In case of forbidden access attempts")
	@APIResponse(responseCode = "500", description = "Internal Server Error")
	public Response banUser(@RestQuery @NotNull final String userId) {
		final Subscriber admin = this.service.findBySubscriberName(accessToken.getSubject());
		
		if (admin == null) {
			return Response.status(Response.Status.NOT_FOUND)
			               .entity("admin not found in database")
			               .build();
		}
		
		final SubscriberDTO bannedDto = this.service.banSubscriber(userId);
		if (bannedDto == null) {
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity("Subscriber hasn't been banned")
			               .build();
		}
		
		return Response.ok(bannedDto)
		               .build();
		
	}
	
}
