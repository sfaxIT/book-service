package de.sfaxit.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import de.sfaxit.model.dto.enums.SubscriberRole;

import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@FieldNameConstants
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubscriberDTO {
	
	@Schema(description = "Username of the subscriber")
	@NotNull
	private String username;
	
	@Schema(description = "Password of the subscriber", required = true)
	@NotNull
	private String password;
	
	@Builder.Default
	@JsonSetter(nulls = Nulls.SKIP)
	@Schema(description = "Role of the subscriber", defaultValue = "READER")
	private SubscriberRole subscriberRole = SubscriberRole.READER;
	
	private Long userId;
	
	@JsonCreator
	public SubscriberDTO(@JsonProperty(value = "username", required = true) final String username,
	                     @JsonProperty(value = "password", required = true) final String password) {
		this.username = username;
		this.password = password;
		this.subscriberRole = SubscriberRole.READER;
	}
	
	@Override
	public String toString() {
		return "SubscriberDTO {" + "username='" + username + '\'' + ", password='" + password + '\'' +
		       ", subscriberRole=" + subscriberRole + ", userId=" + userId + '}';
	}
	
}
