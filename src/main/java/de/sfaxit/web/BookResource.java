package de.sfaxit.web;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status;

import de.sfaxit.model.dto.BookDTO;
import de.sfaxit.model.dto.CollectionResponseDTO;
import de.sfaxit.model.entity.Subscriber;
import de.sfaxit.model.entity.Book;
import de.sfaxit.service.SubscriberService;
import de.sfaxit.service.BookService;

import jakarta.annotation.security.RolesAllowed;
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

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.List;

@Path("books")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RequestScoped
public class BookResource {
	
	@Inject
	BookService bookService;
	
	@Inject
	SubscriberService subscriberService;
	
	@Inject
	JsonWebToken accessToken;
	
	@POST
	@RolesAllowed({"AUTHOR", "ADMIN"})
	@Operation(operationId = "create", description = "Create new book instances for the authenticated subscriber", summary = "Create new book instances")
	@APIResponse(responseCode = "201", description = "In case of successful access attempts. Returns id of the generated message.")
	@APIResponse(responseCode = "400", description = "In case the provided request body doesn't contain a non-empty bookId or the provided bookId already exists")
	@APIResponse(responseCode = "401", description = "In case of unauthorized access attempts")
	@APIResponse(responseCode = "403", description = "In case of forbidden access attempts")
	public Response publishBook(@NotNull @Valid final BookDTO dto) {
		final Subscriber subscriber = this.subscriberService.findBySubscriberName(accessToken.getSubject());
		final Long subscriberId = subscriber.subscriberId;
		
		if (subscriberId == null) {
			return Response.status(Status.UNAUTHORIZED)
			               .entity("Subscriber not found in database")
			               .build();
		}
		
		if (dto.getBookId() != null && this.bookService.bookExists(dto.getBookId())) {
			return Response.status(Status.BAD_REQUEST)
			               .entity("Cannot proceed because book with id " + dto.getBookId() + " was already created")
			               .build();
		}
		
		dto.setSubscriberId(subscriberId);
		final BookDTO persistedBook = this.bookService.publishBook(dto);
		if (persistedBook != null) {
			return Response.status(Status.CREATED)
			               .entity(persistedBook)
			               .build();
		}
		
		return Response.status(Status.NO_CONTENT)
		               .build();
	}
	
	@PUT
	@RolesAllowed({"AUTHOR", "ADMIN"})
	@Operation(operationId = "update", description = "Update an existing book instance", summary = "Update a book")
	@APIResponse(responseCode = "204", description = "In case of successful access attempts")
	@APIResponse(responseCode = "400", description = "In case the requested id and the bookId provided in the request body don't match")
	@APIResponse(responseCode = "401", description = "In case of unauthorized access attempts")
	@APIResponse(responseCode = "403", description = "In case of forbidden access attempts")
	@APIResponse(responseCode = "404", description = "In case the requested entity is unknown yet")
	public Response updateBook(@Valid @NotNull final BookDTO dto) {
		if (dto.getBookId() == null) {
			return Response.status(Status.BAD_REQUEST)
			               .entity("Request body must contain a mandatory bookId")
			               .build();
		}
		
		final Long id = dto.getBookId();
		if (!this.bookService.bookExists(id)) {
			return Response.status(Status.NOT_FOUND)
			               .entity("Cannot update because book is unknown yet")
			               .build();
		}
		
		if (this.bookService.updateBook(dto)) {
			return Response.status(Status.OK)
			               .entity("Book has been successfully updated")
			               .build();
		}
		
		return Response.status(Status.NO_CONTENT)
		               .entity(("Cannot update book"))
		               .build();
	}
	
	@GET
	@RolesAllowed({"AUTHOR", "ADMIN"})
	@Operation(operationId = "get", description = "Retrieve published books requested by authenticated subscriber", summary = "Retrieve own published books")
	@APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/CollectionResponse")))
	@APIResponse(responseCode = "401", description = "In case of unauthorized access attempts")
	@APIResponse(responseCode = "403", description = "In case of forbidden access attempts")
	public Response getAuthorBooks() {
		final Subscriber subscriberEntity = this.subscriberService.findBySubscriberName(accessToken.getSubject());
		if (subscriberEntity == null) {
			return Response.status(Status.NOT_FOUND)
			               .entity("subscriber not found in database")
			               .build();
		}
		
		final List<BookDTO> authorBooks = this.bookService.getBooksByAuthor(subscriberEntity);
		
		return Response.status(Status.OK)
		               .entity(CollectionResponseDTO.of(authorBooks))
		               .build();
	}
	
	@DELETE
	@RolesAllowed({"AUTHOR", "ADMIN"})
	@Operation(operationId = "delete", description = "Delete an existing book instance", summary = "Delete a book")
	@APIResponse(responseCode = "204", description = "In case of successful access attempts")
	@APIResponse(responseCode = "400", description = "In case the requested id is empty")
	@APIResponse(responseCode = "401", description = "In case of unauthorized access attempts")
	@APIResponse(responseCode = "403", description = "In case of forbidden access attempts")
	@APIResponse(responseCode = "404", description = "In case the requested entity is unknown yet")
	public Response unpublishBook(@RestQuery("id") final Long id) {
		if (id == null) {
			return Response.status(Status.BAD_REQUEST)
			               .entity("Request must contain a mandatory book id")
			               .build();
		}
		
		if (!this.bookService.bookExists(id)) {
			return Response.status(Status.NOT_FOUND)
			               .entity("Cannot delete because book is unknown yet")
			               .build();
		}
		
		final Book entity = this.bookService.getBook(id);
		final Subscriber subscriberEntity = this.subscriberService.findBySubscriberName(accessToken.getSubject());
		final Long authorId = subscriberEntity.subscriberId;
		
		if (!this.subscriberService.isBookSubscriberValid(entity, authorId)) {
			return Response.status(Status.FORBIDDEN)
			               .entity("Subscriber with id " + authorId + " is not allowed to delete the book with id " + id)
			               .build();
		}
		
		if (this.bookService.deleteBook(id)) {
			return Response.status(Status.OK)
			               .entity("Deleted book with id " + id + " successfully")
			               .build();
		}
		
		return Response.status(Status.NO_CONTENT)
		               .entity("Cannot delete book with id " + id)
		               .build();
	}
	
}
