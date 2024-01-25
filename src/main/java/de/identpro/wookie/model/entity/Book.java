package de.identpro.wookie.model.entity;

import jakarta.persistence.Column;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@RegisterForReflection(ignoreNested = false)
@Schema(properties = @SchemaProperty(name = "bookId", type = SchemaType.STRING, description = "ID of the published book"), requiredProperties = "bookId")
public class Book extends PanacheEntityBase {

    @Id
    @Column(name = "book_id", nullable = false, length = 50)
    @Schema(description = "Unique id of the published author book")
    public String bookId;

    @Column(name = "book_title")
    @Schema(description = "Title of the published book")
    public String bookTitle;

    @Column(name = "book_description")
    @Schema(description = "Description of the published book")
    public String bookDescription;

    @Column(name = "book_image")
    @Schema(description = "Image of the book cover")
    public byte[] bookCoverImage;

    @Column(name = "book_price")
    @Schema(description = "Sell price of the published book")
    public Double bookPrice;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "author_id", nullable = false, insertable = false, updatable = false)
    public Author author;

}
