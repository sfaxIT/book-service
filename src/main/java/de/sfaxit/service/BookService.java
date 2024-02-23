package de.sfaxit.service;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.find;

import de.sfaxit.model.entity.Book;
import de.sfaxit.model.enums.Role;
import de.sfaxit.model.dto.LoginDTO;
import de.sfaxit.model.entity.Author;
import de.sfaxit.model.dto.SearchResultHolderDTO;
import de.sfaxit.util.TokenGenerator;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class BookService {
    private static final Logger LOG = LoggerFactory.getLogger(BookService.class);

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

    public SearchResultHolderDTO findAllBooksByPageSize(final Integer pageSize) {
        // Restrict fetching the number of books based on given page size
        var queryBySize = Book.findAll().page(Page.ofSize(pageSize));

        final List<Book> result = new ArrayList<>(queryBySize.list());

        while (queryBySize.hasNextPage()) {
            queryBySize = queryBySize.nextPage();

            result.addAll(queryBySize.list());
        }

        return SearchResultHolderDTO.builder()
                                    .currentPage(queryBySize.page().index)
                                    .pageCount(queryBySize.pageCount())
                                    .pageSize(queryBySize.page().size)
                                    .totalCount(queryBySize.count())
                                    .books(result)
                                    .build();
    }

    public SearchResultHolderDTO findBooksBySearchTerm(final int pageIndex, final Integer pageSize, final String searchTerm) {
        final PanacheQuery<Book> queryBySearchTerm =
                Book.find("{[ {'bookTitle': {'$bookregex': ?1, '$options': 'i'}}, {'bookDescription': {'$bookregex': ?1, '$options': 'i'}}, {'bookPrice': {'$bookregex': ?1, '$options': 'i'}}, {'author': {'$bookregex': ?2, '$options': 'i'}}] }", searchTerm).firstResult();

/*        if (size == null) {
            query.page(pageIndex, Integer.MAX_VALUE);
        } else {*/
        final PanacheQuery<Book> queryByPage = queryBySearchTerm.page(pageIndex, pageSize);
/*        }*/

        return SearchResultHolderDTO.builder()
                                 .currentPage(queryByPage.page().index)
                                 .pageCount(queryByPage.pageCount())
                                 .pageSize(queryByPage.page().size)
                                 .totalCount(queryByPage.count())
                                 .books(queryByPage.list())
                                 .build();
    }

}
