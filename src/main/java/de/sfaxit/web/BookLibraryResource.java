package de.sfaxit.web;

import java.util.List;

import de.sfaxit.model.entity.Book;
import de.sfaxit.service.WookieBookService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookLibraryResource {

    @Inject
    WookieBookService service;

    @GET
    @Path("/all")
    @Operation(description = "Retrieve a list of all books from the library", summary = "Retrieve all available books")
    @APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json"))
    @APIResponse(responseCode = "500", description = "Internal Server Error")
    public Response getAllBooks() {
        final List<Book> allBooks = this.service.findAllBooks();

        return Response.ok(allBooks)
                       .build();
    }

/*    @GET
    @Path("/search")
    @Operation(description = "Searche for all active productionOrder instances by productionOrderId, name or description containing the provided term implementing page-based pagination", summary = "Searches for productionOrder instances by given term")
    @APIResponse(responseCode = "200", description = "In case of successful access attempts", content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/PagedCollectionResponse")))
    @APIResponse(responseCode = "401", description = "In case of unauthorized access attempts")
    public Response pageBasedSearch(@RestQuery @DefaultValue(EMPTY) final String tenantId,
            @RestQuery @DefaultValue("0") @Parameter(schema = @Schema(defaultValue = "0", type = SchemaType.INTEGER, format = "int32")) final Integer page,
            @RestQuery @Min(1) final Integer size,
            @RestQuery("searchBy") @RegEx @DefaultValue(EMPTY) final String searchTerm) {

        final SearchResultHolder searchResult = this.service.find(tenantId, page, size, searchTerm);

        return Response.status(Response.Status.OK)
                       .entity(PagedCollectionResponse.of(searchResult.getMessageResult(), searchResult.getPageCount(),
                               searchResult.getCurrentPage(), searchResult.getTotalCount()))
                       .build();
    }*/

}
