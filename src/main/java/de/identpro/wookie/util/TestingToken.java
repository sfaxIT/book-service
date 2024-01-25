package de.identpro.wookie.util;

import java.time.Instant;

import io.smallrye.jwt.build.Jwt;

import io.smallrye.jwt.build.JwtClaimsBuilder;
import org.eclipse.microprofile.jwt.Claims;

public class TestingToken {
    public static void main(String[] args) {
        // Create an empty builder and add some claims
        final JwtClaimsBuilder builder = Jwt.claims();
        builder.claim(Claims.preferred_username.name(), "username");
        builder.upn("test");
        builder.issuer("https://identpro.de");
        builder.issuedAt(Instant.now());
        builder.expiresIn(600L);


        final String jwt = builder.jws().innerSign().encrypt();

/*        final String claims = Jwt.issuer("https://identpro.de")
                                 .upn("test")
                                 .groups(new HashSet<>(Arrays.asList(Role.AUTHOR.name(), Role.ADMIN.name())))
                                 .claim(Claims.preferred_username.name(), "username")
                                 .claim(Claims.nickname.name(), "authorPseudonym")
                                 .issuedAt(Instant.now())
                                 .expiresIn(6000)
                                 .sign();

        final String token = Jwt.*/
        /*        return Jwt.claims(claims).innerSign().encrypt();*/
        System.out.println(jwt);
    }

}
