package de.identpro.wookie.service;

import de.identpro.wookie.model.enums.Role;
import de.identpro.wookie.model.dto.LoginDTO;
import de.identpro.wookie.model.entity.Author;
import de.identpro.wookie.util.TokenGenerator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.find;

@ApplicationScoped
public class WookieBookService {
    private static final Logger LOG = LoggerFactory.getLogger(WookieBookService.class);

    @Inject
    TokenGenerator tokenGenerator;

    public Author findUser(final String username) {
        return find("author_username", username).firstResult();
    }

    public boolean exists(final String username) {
        return find("author_username", username) != null;
    }

    public boolean isValidPassword(final Author user, final String pwd) {
        return user.authorPassword.equals(pwd);
    }

    public LoginDTO getUserToken(final Author user) {
        return this.tokenGenerator.generateUserJWT(user);
    }

    public Author registerUser(final String username, final String password, final Role role) {
        try {
            final Author authorEntity = Author.builder()
                                              .authorName(username)
                                              .authorPassword(password)
                                              .authorRole(role)
                                              .build();

            authorEntity.persist();
            LOG.debug("Persisted user {}", authorEntity);

            return authorEntity;
        } catch (final Exception e) {
            LOG.error("registerUser Error {}", e.getMessage());
        }
        return null;
    }

}
