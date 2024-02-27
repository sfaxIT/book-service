package de.sfaxit.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

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
public class LoginDTO {
	
	@Schema(description = "User JWT")
	private String token;
}
