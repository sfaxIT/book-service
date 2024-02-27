package de.sfaxit.web;


import static org.apache.commons.lang3.StringUtils.EMPTY;

import de.sfaxit.model.dto.PagedCollectionResponseDTO;
import de.sfaxit.service.BookService;
import de.sfaxit.model.dto.SearchResultHolderDTO;
import de.sfaxit.util.validation.BookRegEx;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Min;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestQuery;

@Path("books/library")
@Produces(MediaType.APPLICATION_JSON)
public class BookLibraryResource {
	
	@Inject
	BookService service;
	
	@GET
	@Path("all")
	@Operation(description = "Retrieve a list of all books from the library", summary = "Retrieve all library books")
	@APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json"))
	@APIResponse(responseCode = "400", description = "In case the provided request contains an empty library")
	@APIResponse(responseCode = "500", description = "Internal Server Error")
	public Response readAllBooks(@Min(10) final Integer size) {
		
		final SearchResultHolderDTO allBooks = this.service.findAllBooksByPageSize(size);
		
		if (allBooks == null) {
			return Response.status(Response.Status.BAD_REQUEST)
			               .entity("Book library is empty")
			               .build();
		}
		
		return Response.status(Response.Status.OK)
		               .entity(PagedCollectionResponseDTO.of(allBooks.getBooks(), allBooks.getPageCount(),
		                                                     allBooks.getCurrentPage(), allBooks.getTotalCount()))
		               .build();
	}
	
	@GET
	@Path("search")
	@Operation(description = "Search for all books instances by book title, description, price or author name containing the provided term implementing page-based pagination", summary = "Search for book instances by given term")
	@APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/PagedCollectionResponseDTO")))
	@APIResponse(responseCode = "400", description = "In case the provided request contains an empty table")
	@APIResponse(responseCode = "500", description = "Internal Server Error")
	public Response searchBooks(@RestQuery @DefaultValue("0") @Parameter(schema = @Schema(defaultValue = "0", type = SchemaType.INTEGER, format = "int32")) final Integer page,
	                            @RestQuery @Min(10) final Integer size,
	                            @RestQuery("searchBy") @BookRegEx @DefaultValue(EMPTY) final String searchTerm) {
		
		final SearchResultHolderDTO searchResult = this.service.findBooksBySearchTerm(page, size, searchTerm);
		
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
