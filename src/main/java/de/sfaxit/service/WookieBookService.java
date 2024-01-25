package de.sfaxit.service;

import de.sfaxit.model.entity.Book;
import de.sfaxit.model.enums.Role;
import de.sfaxit.model.dto.LoginDTO;
import de.sfaxit.model.entity.Author;
import de.sfaxit.util.TokenGenerator;

import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.find;

import java.util.ArrayList;
import java.util.List;

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

    public List<Book> findAllBooks() {
        final List<Book> result = new ArrayList<>();

        var page = Book.findAll()
                       .page(Page.ofSize(100));

        result.addAll(page.list());

        while (page.hasNextPage()) {
            page = page.nextPage();
            result.addAll(page.list());
        }

        return result;
    }

}
