package de.sfaxit.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import de.sfaxit.model.dto.enums.UserRole;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@FieldNameConstants
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDTO {
	
	@Schema(description = "Username of the author")
	private final String username;
	
	@Schema(description = "Password of the author", required = true)
	private final String password;
	
	@Builder.Default
	@JsonSetter(nulls = Nulls.SKIP)
	@Schema(description = "Role of the user", defaultValue = "AUTHOR")
	private UserRole userRole = UserRole.AUTHOR;
	
	private Long userId;
	
	@JsonCreator
	public UserDTO(@JsonProperty(value = "username", required = true) final String username,
	               @JsonProperty(value = "password", required = true) final String password) {
		this.username = username;
		this.password = password;
		this.userRole = UserRole.AUTHOR;
	}
	
}
