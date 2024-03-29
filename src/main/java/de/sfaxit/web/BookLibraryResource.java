package de.sfaxit.web;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import de.sfaxit.model.dto.PagedCollectionResponseDTO;
import de.sfaxit.service.BookService;
import de.sfaxit.model.dto.SearchResultHolderDTO;
import de.sfaxit.util.validation.ValidBookSearch;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestQuery;

@Path("books/library")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class BookLibraryResource {
	
	@Inject
	BookService service;
	
	@GET
	@Path("all")
	@RolesAllowed({"ADMIN", "AUTHOR", "READER"})
	@Operation(description = "Retrieve a list of all books from the library", summary = "Retrieve all library books")
	@APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/PagedCollectionResponseDTO")))
	@APIResponse(responseCode = "400", description = "In case the provided request contains an empty library")
	@APIResponse(responseCode = "500", description = "Internal Server Error")
	public Response readAllBooks(@RestQuery @Min(2) final Integer size) {
		final SearchResultHolderDTO allBooks = this.service.findAllBooksByPage(size);
		
		if (allBooks == null) {
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity("Book library is empty")
			               .build();
		}
		
		return Response.status(Response.Status.OK)
		               .entity(PagedCollectionResponseDTO.of(allBooks.getBooks(), allBooks.getPageCount(), allBooks.getCurrentPage(), allBooks.getTotalCount()))
		               .build();
	}
	
	@GET
	@Path("search")
	@RolesAllowed({"ADMIN", "AUTHOR", "READER"})
	@Operation(description = "Search for all books instances by book title, description, price or subscriber name containing the provided term implementing page-based pagination", summary = "Search for book instances by given term")
	@APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/PagedCollectionResponseDTO")))
	@APIResponse(responseCode = "400", description = "In case the provided request contains an empty table")
	@APIResponse(responseCode = "500", description = "Internal Server Error")
	public Response searchBooks(@RestQuery @Min(2) final Integer size,
	                            @RestQuery("searchBy") @ValidBookSearch final String searchTerm,
	                            @RestQuery("searchByValue") @DefaultValue(EMPTY) final String termValue) {
		
		final SearchResultHolderDTO searchResult = this.service.findBooksBySearchTerm(size, searchTerm, termValue);
		
		if (searchResult == null) {
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity("Book library is empty")
			               .build();
		}
		
		return Response.status(Response.Status.OK)
		               .entity(PagedCollectionResponseDTO.of(searchResult.getBooks(), searchResult.getPageCount(),
		                                                     searchResult.getCurrentPage(),
		                                                     searchResult.getTotalCount()))
		               .build();
	}
	
}
