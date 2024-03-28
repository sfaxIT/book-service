package de.sfaxit.util;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;

import de.sfaxit.model.dto.LoginDTO;
import de.sfaxit.model.entity.Author;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;

import jakarta.enterprise.context.RequestScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class TokenGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(TokenGenerator.class);
	
	public LoginDTO generateUserJWT(final Author user) {
		try {
			// Create an empty builder and add some claims
			final JwtClaimsBuilder builder = Jwt.claims();
			builder.subject(user.username);
/*			builder.groups(user.authorRole);*/
/*			builder.claim("pwd", user.pwd);*/
			builder.groups(new HashSet<>(Arrays.asList(user.authorRole)));
/*			builder.upn("test");
			builder.issuer("https://sfaxit.de");
			builder.issuedAt(Instant.now());*/
/*			builder.expiresIn(600L);*/
			
/*			final String jwt = builder.jws()
			                          .innerSign()
			                          .encrypt();*/
			
			final String jwt = Jwt.subject(user.getUsername())
			                      .issuer("https://sfaxit.de")
			                      .groups(new HashSet<>(Arrays.asList(user.authorRole)))
			                      .expiresIn(600L)
			                      .sign();
			
			LOG.info("Generated Json Web Token {}", jwt);
			
			return LoginDTO.builder()
			               .token(jwt)
			               .build();
			
		} catch (final Exception e) {
			LOG.error("generateUserJWT Error {}", e.getMessage());
		}
		return null;
	}
	
}
