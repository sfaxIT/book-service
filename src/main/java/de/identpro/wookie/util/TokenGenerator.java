package de.identpro.wookie.util;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;

import de.identpro.wookie.model.Role;
import de.identpro.wookie.model.User;

import io.smallrye.jwt.build.Jwt;

import jakarta.enterprise.context.RequestScoped;

import org.eclipse.microprofile.jwt.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class TokenGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(TokenGenerator.class);

    public String generateUserJWT(final User user) {
        return Jwt.issuer("https://identpro.de")
                  .groups(new HashSet<>(Arrays.asList(Role.AUTHOR.name(), Role.ADMIN.name())))
                  .claim(Claims.preferred_username.name(), user.username)
                  .claim(Claims.nickname.name(), user.authorPseudonym)
                  .issuedAt(Instant.now())
                  .expiresIn(600)
                  .sign();
    }

}
