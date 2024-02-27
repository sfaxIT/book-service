package de.sfaxit.model.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import de.sfaxit.model.enums.Role;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@RegisterForReflection(ignoreNested = false)
@Schema(properties = @SchemaProperty(name = "authorId", type = SchemaType.STRING, description = "ID of the author"), requiredProperties = "authorId")
public class Author extends PanacheEntityBase {
	
	@Id
	@Column(name = "author_id", nullable = false, length = 50)
	@Schema(description = "Unique id of the author")
	public String authorId;
	
	@Column(name = "author_username")
	@NotNull
	@Size(max = 50)
	@Schema(description = "Username of the author", required = true)
	public String authorName;
	
	@Column(name = "author_password")
	@NotNull
	@Size(max = 50)
	@Schema(description = "Password of the author", required = true)
	public String authorPassword;
	
	@Column(name = "author_role")
	@Size(max = 50)
	@Builder.Default
	@JsonSetter(nulls = Nulls.SKIP)
	@Schema(description = "Role of the author", defaultValue = "AUTHOR")
	public Role authorRole = Role.AUTHOR;
	
	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<Book> authorBooks = new HashSet<>();
	
	public void addBook(final Book book) {
		getAuthorBooks().add(book);
		book.setAuthor(this);
	}
	
	public void removeBook(final Book book) {
		getAuthorBooks().remove(book);
		book.setAuthor(null);
	}
	
	public static Author findByName(final String username) {
		return find("authorName", username).firstResult();
	}
	
	@JsonCreator
	public Author(@JsonProperty(value = "username", required = true) final String username,
	              @JsonProperty(value = "password", required = true) final String password) {
		this.authorName = username;
		this.authorPassword = password;
		this.authorRole = Role.AUTHOR;
		this.authorBooks = new HashSet<>();
	}
	
}
