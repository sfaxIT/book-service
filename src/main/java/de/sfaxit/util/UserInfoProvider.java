package de.sfaxit.util;

import de.sfaxit.model.dto.UserDTO;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class UserInfoProvider {
	
	@Inject
	JsonWebToken jwt;
	
	public UserDTO getUser() {
		final String firstName = this.jwt.getClaim(Claims.given_name);
		final String lastName = this.jwt.getClaim(Claims.family_name);
		final String username = this.jwt.getClaim(Claims.preferred_username);
		
		return UserDTO.builder()
		              .username(username)
		              .build();
	}
	
}
