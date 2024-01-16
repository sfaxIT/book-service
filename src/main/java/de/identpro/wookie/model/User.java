package de.identpro.wookie.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@RegisterForReflection(ignoreNested = false)
public class User extends PanacheEntity {
    public String username;
    public String password;
    public String authorPseudonym;
    public Role role;

    public static User findByName(final String username) {
        return find("username", username).firstResult();
    }

}
