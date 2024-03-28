package de.sfaxit.service;

import de.sfaxit.model.dto.BookDTO;
import de.sfaxit.model.dto.LoginDTO;
import de.sfaxit.model.dto.SearchResultHolderDTO;
import de.sfaxit.model.dto.UserDTO;
import de.sfaxit.model.dto.enums.UserRole;
import de.sfaxit.model.entity.Author;
import de.sfaxit.model.entity.Book;
import de.sfaxit.util.BookPersister;
import de.sfaxit.util.TokenGenerator;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@ApplicationScoped
public class AuthorService {
	private static final Logger LOG = LoggerFactory.getLogger(AuthorService.class);
	
	@Inject
	TokenGenerator tokenGenerator;
	
	@Inject
	BookPersister persister;
	
	public boolean authorExists(@NotNull final String username) {
		return Author.findByName(username) != null;
	}
	
	public Author findByUsername(@NotNull final String username) {
		return Author.findByName(username);
	}
	
	public boolean isValidPassword(@NotNull final Author user, @NotNull final String pwd) {
		return StringUtils.equals(user.pwd, pwd);
	}
	
	public Author registerUser(final String username, final String password, final UserRole authorRole) {
		try {
			return this.persister.addUser(username, password, authorRole);
		} catch (final Exception e) {
			LOG.error("registerUser Error {}", e.getMessage());
		}
		return null;
	}
	
	public LoginDTO getUserToken(@NotNull final Author user) {
		return this.tokenGenerator.generateUserJWT(user);
	}
	
	public boolean isBookAuthorValid(@NotNull final Book bookEntity, @NotNull final Long userId) {
		if (bookEntity.author != null) {
			final Long bookAuthorId = bookEntity.author.authorId;
			return userId.equals(bookAuthorId);
		}
		return false;
	}
	
	public SearchResultHolderDTO findAllAuthorsByPage(final Integer pageSize) {
		PanacheQuery<Author> authorsPage = Author.findAll(Sort.by("authorId", Sort.Direction.Descending))
		                                         .page(Page.ofSize(pageSize));
		
		final List<Author> result = new ArrayList<>(authorsPage.list());
		
		while (authorsPage.hasNextPage()) {
			authorsPage = authorsPage.nextPage();
			
			result.addAll(authorsPage.list());
		}
		LOG.info("Author result count {}", result.size());
		
		final List<UserDTO> authors = new ArrayList<>();
		emptyIfNull(result).forEach(entity -> {
			final UserDTO dto = this.mapAuthorEntityToDto(entity);
			authors.add(dto);
		});
		LOG.info("UserDTO count {}", authors.size());
		
		return SearchResultHolderDTO.builder()
		                            .currentPage(authorsPage.page().index)
		                            .pageCount(authorsPage.pageCount())
		                            .pageSize(authorsPage.page().size)
		                            .totalCount(authorsPage.count())
		                            .users(authors)
		                            .build();
	}
	
	private UserDTO mapAuthorEntityToDto(final Author entity) {
		if (entity != null) {
			return UserDTO.builder()
			              .userId(entity.authorId)
			              .username(entity.username)
			              .password(entity.pwd)
			              .userRole(UserRole.of(entity.authorRole))
			              .build();
		}
		LOG.info("mapAuthorEntityToDto: Couldn't create Book entity");
		return null;
	}
	
}
