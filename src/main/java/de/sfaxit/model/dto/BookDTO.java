package de.sfaxit.model.dto;

import static de.sfaxit.model.entity.Book.DATE_FORMAT;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.Nulls;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Arrays;
import java.util.HexFormat;

@Getter
@Setter
@FieldNameConstants
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookDTO {
	
	@Size(min = 5, max = 50, message = "Title size must be between 5 and 50 characters")
	@Schema(description = "Book title", required = true)
	private String title;
	
	@Size(min = 10, max = 200, message = "Book description size must be between 5 and 200 characters")
	@Schema(description = "Book description")
	private String description;
	
	@Builder.Default
	@JsonSetter(nulls = Nulls.SKIP)
	@Schema(description = "Book cover", defaultValue = "e0:4f:d0:20:ea:3a:69:10:a2:d8:08:00:2b:30:30:9d")
	private byte[] cover = HexFormat.ofDelimiter(":")
	                                .parseHex("e0:4f:d0:20:ea:3a:69:10:a2:d8:08:00:2b:30:30:9d");
	
	@Schema(description = "Book price", required = true)
	@Positive
	@NotBlank(message = "Book price cannot be blank")
	private String price;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	@Schema(description = "Published date of a book - ISO 8601 representation (UTC)", example = "2024-01-01T10:00:00Z")
	public String publishDate;
	
	private Long bookId;
	private Long subscriberId;
	
	@JsonCreator
	public BookDTO(@JsonProperty(value = "title", required = true) final String title,
	               @JsonProperty(value = "description", required = true) final String description,
	               @JsonProperty(value = "price", required = true) final String price) {
		
		this.title = title;
		this.description = description;
		this.price = price;
		this.cover = HexFormat.ofDelimiter(":")
		                      .parseHex("e0:4f:d0:20:ea:3a:69:10:a2:d8:08:00:2b:30:30:9d");
	}
	
	@Override
	public String toString() {
		return "BookDTO {" + "title='" + title + '\'' + ", description='" + description + '\'' + ", cover=" +
		       Arrays.toString(cover) + ", price=" + price + ", publishDate='" + publishDate + '\'' + ", bookId=" +
		       bookId + ", subscriberId=" + subscriberId + '}';
	}
	
}
