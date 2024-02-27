package de.sfaxit.web;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status;

import de.sfaxit.model.dto.BookDTO;
import de.sfaxit.model.dto.CollectionResponseDTO;
import de.sfaxit.model.entity.Author;
import de.sfaxit.model.entity.Book;
import de.sfaxit.service.BookService;

import io.quarkus.security.Authenticated;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.List;

@Authenticated
@Path("books")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RequestScoped
public class BookResource {
	
	@Inject
	BookService service;
	
	@Inject
	@Claim(standard = Claims.preferred_username)
	String username;
	
	@POST
	@Operation(operationId = "create", description = "Create new book instances for the authenticated author", summary = "Create new book instances")
	@APIResponse(responseCode = "201", description = "In case of successful access attempts. Returns id of the generated message.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDTO.class)))
	@APIResponse(responseCode = "400", description = "In case the provided request body doesn't contain a non-empty bookId or the provided bookId already exists")
	@APIResponse(responseCode = "401", description = "In case of unauthorized access attempts")
	public Response publishBook(@Valid @NotNull final BookDTO dto) {
		final Author authorEntity = this.service.findUser(username);
		if (authorEntity == null) {
			return Response.status(Status.NOT_FOUND)
			               .entity("bookAuthor is null")
			               .build();
		}
		
		// Prevent the user with the username darth-vader to publish any book
		if (username.equals("sfaxit")) {
			return Response.status(Status.FORBIDDEN)
			               .entity("User with username " + username + " is not allowed to publish any book")
			               .build();
		}
		
		if (this.service.bookExists(dto.getBookId())) {
			return Response.status(Status.BAD_REQUEST)
			               .entity("Cannot proceed because book with id " + dto.getBookId() + " was already created")
			               .build();
		}
		
		if (this.service.publishBook(dto, authorEntity) != null) {
			return Response.status(Status.CREATED)
			               .entity(dto)
			               .build();
		}
		
		return Response.status(Status.NO_CONTENT)
		               .build();
	}
	
	@PUT
	@Operation(operationId = "update", description = "Update an existing book instance", summary = "Update a book")
	@APIResponse(responseCode = "204", description = "In case of successful access attempts")
	@APIResponse(responseCode = "400", description = "In case the requested id and the bookId provided in the request body don't match")
	@APIResponse(responseCode = "401", description = "In case of unauthorized access attempts")
	@APIResponse(responseCode = "404", description = "In case the requested entity is unknown yet")
	public Response updateBook(@Valid @NotNull final BookDTO dto) {
		if (dto.getBookId() == null || dto.getBookId().isEmpty()) {
			return Response.status(Status.BAD_REQUEST)
			               .entity("Request body must contain a mandatory bookId")
			               .build();
		}
		
		final String id = dto.getBookId();
		if (!this.service.bookExists(id)) {
			return Response.status(Status.NOT_FOUND)
			               .entity("Cannot update because book is unknown yet")
			               .build();
		}
		
		if (this.service.updateBook(dto)) {
			return Response.status(Status.OK)
			               .entity("Book has been successfully updated")
			               .build();
		}
		
		return Response.status(Status.NO_CONTENT)
		               .entity(("Cannot update book"))
		               .build();
	}
	
	@GET
	@Operation(operationId = "get", description = "Retrieve published books requested by authenticated author", summary = "Retrieve own published books")
	@APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDTO.class)))
	@APIResponse(responseCode = "401", description = "In case of unauthorized access attempts")
	@APIResponse(responseCode = "404", description = "In case the requested entity is unknown yet")
	public Response getAuthorBooks() {
		final Author authorEntity = this.service.findUser(username);
		if (authorEntity == null) {
			return Response.status(Status.NOT_FOUND)
			               .entity("book author is null")
			               .build();
		}
		
		final List<BookDTO> authorBooks = this.service.getBooksByAuthor(authorEntity);
		
		return Response.status(Status.OK)
		               .entity(CollectionResponseDTO.of(authorBooks))
		               .build();
	}
	
	@DELETE
	@Operation(operationId = "delete", description = "Delete an existing book instance", summary = "Delete a book")
	@APIResponse(responseCode = "204", description = "In case of successful access attempts")
	@APIResponse(responseCode = "400", description = "In case the requested id is empty")
	@APIResponse(responseCode = "401", description = "In case of unauthorized access attempts")
	@APIResponse(responseCode = "404", description = "In case the requested entity is unknown yet")
	public Response unpublishBook(@RestQuery("id") final String id) {
		if (id == null || id.isEmpty()) {
			return Response.status(Status.BAD_REQUEST)
			               .entity("Request must contain a mandatory book id")
			               .build();
		}
		
		if (!this.service.bookExists(id)) {
			return Response.status(Status.NOT_FOUND)
			               .entity("Cannot delete because book is unknown yet")
			               .build();
		}
		
		final Book entity = this.service.getBook(id);
		
		if (!this.service.validateBookAuthor(entity, username)) {
			return Response.status(Status.FORBIDDEN)
			               .entity("User with username " + username + " is not allowed to delete the book with id " + id)
			               .build();
		}
		
		if (this.service.deleteBook(entity)) {
			return Response.status(Status.OK)
			               .entity("Deleted book with id " + id + " successfully")
			               .build();
		}
		
		return Response.status(Status.NO_CONTENT)
		               .entity("Cannot delete book with id " + id)
		               .build();
	}
	
}
