package de.sfaxit.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection(ignoreNested = false)
@Schema(properties = @SchemaProperty(name = "bookId", type = SchemaType.STRING, description = "ID of the published book"), requiredProperties = "bookId")
public class Book extends PanacheEntityBase {
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_id", nullable = false, length = 50)
	@Schema(description = "Unique id of the published subscriber book")
	public Long bookId;
	
	@Column(name = "book_title")
	@Schema(description = "Title of the published book")
	@Size(max = 255)
	public String title;
	
	@Column(name = "book_description")
	@Schema(description = "Description of the published book")
	public String description;
	
	@Column(name = "book_image")
	@Schema(description = "Image of the book cover")
	public byte[] coverImage;
	
	@Column(name = "book_price" )
	@Schema(description = "Sell price of the published book")
	public BigDecimal price;
	
	@Column(name = "publish_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	@Schema(description = "Published date of a book - ISO 8601 representation (UTC)", example = "2024-01-01T10:00:00Z")
	public String publishDate;
	
	@ManyToOne
	@JoinColumn(name = "subscriber_id", referencedColumnName = "subscriber_id", nullable = false)
	public Subscriber subscriber;
	
	public static PanacheQuery<Book> findByTitle(final String searchByTerm, final String termValue, final Integer pageSize) {
		return Book.find(searchByTerm, termValue)
		           .page(Page.ofSize(pageSize));
	}
	
	@Override
	public String toString() {
		return "Book {" + "bookId=" + bookId + ", title='" + title + '\'' + ", description='" + description + '\'' +
		       ", coverImage=" + Arrays.toString(coverImage) + ", price=" + price + ", publishDate='" + publishDate +
		       '\'' + ", subscriber=" + subscriber + '}';
	}
	
}
