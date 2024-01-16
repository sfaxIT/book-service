package de.identpro.wookie.web;

import de.identpro.wookie.model.User;
import de.identpro.wookie.service.WookieBookService;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;

import org.jboss.resteasy.reactive.RestQuery;

@Path("/auth")
public class AuthenticationResource {

    @Inject
    WookieBookService service;

    @GET
    @Path("/login")
    public Response login(@RestQuery final String username, @RestQuery final String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Request must contain the mandatory credentials username and password")
                           .build();
        }

        if (!this.service.exists(username)) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("User not found with username {}" + username)
                           .build();
        }

        final User registeredUser = this.service.findUser(username);
        if (!this.service.isValidPassword(registeredUser, password)) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Wrong password")
                           .build();
        }

        return Response.ok(this.service.getUserToken(registeredUser))
                       .build();
    }




}
