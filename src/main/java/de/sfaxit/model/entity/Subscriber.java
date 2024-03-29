package de.sfaxit.model.entity;

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
@Schema(properties = @SchemaProperty(name = "subscriberId", type = SchemaType.STRING, description = "ID of the books library subscriber"), requiredProperties = "subscriberId")
public class Subscriber extends PanacheEntityBase {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subscriber_id", nullable = false, length = 50)
	@Schema(description = "Unique id of the subscriber")
	public Long subscriberId;
	
	@Column(name = "subscriber_name")
	@NotNull
	@Size(max = 50)
	@Schema(description = "Name of the subscriber", required = true)
	public String subscriberName;
	
	@Column(name = "subscriber_password")
	@NotNull
	@Size(max = 50)
	@Schema(description = "Password of the subscriber", required = true)
	public String subscriberPassword;
	
/*	@Enumerated(EnumType.STRING)
	@Column(name = "author_role", columnDefinition = "enum")*/
	@Column(name = "subscriber_role")
	@Size(max = 50)
	@Schema(description = "Role of the subscriber")
	public String subscriberRole;
	
	@OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Book> subscriberBooks = new HashSet<>();
	
	public void addBook(final Book book) {
		getSubscriberBooks().add(book);
		book.setSubscriber(this);
	}
	
	public void removeBook(final Book book) {
		getSubscriberBooks().remove(book);
		book.setSubscriber(null);
	}
	
	public static Subscriber findByName(final String subscriberName) {
		return find("subscriberName", subscriberName).firstResult();
	}
	
	public static long delete(final String name) {
		return delete("subscriberName", name);
	}
	
	@JsonCreator
	public Subscriber(@JsonProperty(value = "name", required = true) final String subscriberName,
	                  @JsonProperty(value = "password", required = true) final String subscriberPassword) {
		this.subscriberName = subscriberName;
		this.subscriberPassword = subscriberPassword;
		this.subscriberBooks = new HashSet<>();
	}
	
	@Override
	public String toString() {
		return "Subscriber {" + "subscriberId=" + subscriberId + ", subscriberName='" + subscriberName + '\'' +
		       ", subscriberPassword='" + subscriberPassword + '\'' + ", subscriberRole='" + subscriberRole + '\'' +
		       ", subscriberBooks=" + subscriberBooks + '}';
	}
	
}
