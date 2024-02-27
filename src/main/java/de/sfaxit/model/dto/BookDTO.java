package de.sfaxit.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HexFormat;

@Getter
@FieldNameConstants
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookDTO {
	
	@Schema(description = "Book title")
	@Min(20)
	@Max(50)
	private final String title;
	
	@Schema(description = "Book description")
	@Max(200)
	private String description;
	
	@Builder.Default
	@JsonSetter(nulls = Nulls.SKIP)
	@Schema(description = "Book title", defaultValue = "e0:4f:d0:20:ea:3a:69:10:a2:d8:08:00:2b:30:30:9d")
	private byte[] cover = HexFormat.ofDelimiter(":")
	                                .parseHex("e0:4f:d0:20:ea:3a:69:10:a2:d8:08:00:2b:30:30:9d");
	
	@Schema(description = "Book price")
	@Positive
	private final Double price;
	
	private String bookId;
	private String authorId;
	
	public BookDTO(@JsonProperty(value = "title", required = true) final String title,
	               @JsonProperty(value = "description", required = true) final String description,
	               @JsonProperty(value = "price", required = true) final Double price) {
		this.title = title;
		this.description = description;
		this.price = price;
		this.cover = HexFormat.ofDelimiter(":")
		                      .parseHex("e0:4f:d0:20:ea:3a:69:10:a2:d8:08:00:2b:30:30:9d");
	}
	
}
