package de.sfaxit.model.entity;

import de.sfaxit.model.dto.enums.UserRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection(ignoreNested = false)
@Schema(properties = @SchemaProperty(name = "authorId", type = SchemaType.STRING, description = "ID of the book author"), requiredProperties = "authorId")
public class Author extends PanacheEntityBase {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "author_id", nullable = false, length = 50)
	@Schema(description = "Unique id of the author")
	public Long authorId;
	
	@Column(name = "author_username")
	@NotNull
	@Size(max = 50)
	@Schema(description = "Username of the author", required = true)
	public String username;
	
	@Column(name = "author_password")
	@NotNull
	@Size(max = 50)
	@Schema(description = "Password of the author", required = true)
	public String pwd;
	
/*	@Enumerated(EnumType.STRING)
	@Column(name = "author_role", columnDefinition = "enum")*/
	@Column(name = "author_role")
	@Size(max = 50)
	@Schema(description = "Role of the author", defaultValue = "AUTHOR")
	public String authorRole = UserRole.AUTHOR.name();
	
	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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
		return find("username", username).firstResult();
	}
	
	@JsonCreator
	public Author(@JsonProperty(value = "username", required = true) final String username,
	              @JsonProperty(value = "password", required = true) final String password) {
		this.username = username;
		this.pwd = password;
		this.authorRole = UserRole.AUTHOR.name();
		this.authorBooks = new HashSet<>();
	}
	
	@Override
	public String toString() {
		return "Author {" + "authorId=" + authorId + ", username='" + username + '\'' + ", pwd='" + pwd + '\'' +
		       ", authorRole='" + authorRole + '\'' + ", authorBooks=" + authorBooks + '}';
	}
	
}
