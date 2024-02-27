package de.sfaxit.service;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.find;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import de.sfaxit.model.dto.BookDTO;
import de.sfaxit.model.entity.Book;
import de.sfaxit.model.enums.Role;
import de.sfaxit.model.dto.LoginDTO;
import de.sfaxit.model.entity.Author;
import de.sfaxit.model.dto.SearchResultHolderDTO;
import de.sfaxit.util.BookPersister;
import de.sfaxit.util.TokenGenerator;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class BookService {
	private static final Logger LOG = LoggerFactory.getLogger(BookService.class);
	
	@Inject
	TokenGenerator tokenGenerator;
	
	@Inject
	BookPersister persister;
	
	public Author findUser(final String username) {
		return find("author_username", username).firstResult();
	}
	
	public boolean AuthorExists(final String username) {
		return find("author_username", username) != null;
	}
	
	public boolean bookExists(String id) {
		return find("book_id", id) != null;
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
		var queryBySize = Book.findAll()
		                      .page(Page.ofSize(pageSize));
		
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
	
	public SearchResultHolderDTO findBooksBySearchTerm(final int pageIndex,
	                                                   final Integer pageSize,
	                                                   final String searchTerm) {
		final PanacheQuery<Book> queryBySearchTerm = Book.find(
				                                                 "{[ {'bookTitle': {'$bookregex': ?1, '$options': 'i'}}, {'bookDescription': {'$bookregex': ?1, '$options': 'i'}}, {'bookPrice': {'$bookregex': ?1, '$options': 'i'}}, {'author': {'$bookregex': ?2, '$options': 'i'}}] }",
				                                                 searchTerm)
		                                                 .firstResult();

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
	
	public BookDTO publishBook(final BookDTO dto, final Author authorEntity) {
		final Book bookEntity = this.convertDtoToEntity(dto, authorEntity);
		
		if (bookEntity == null) {
			return null;
		}
		
		final Book dbBook = this.persister.create(bookEntity);
		LOG.info("publishBook with id {}", dbBook.bookId);
		
		return BookDTO.builder()
		              .title(dbBook.bookTitle)
		              .description(dbBook.bookDescription)
		              .price(dbBook.bookPrice)
		              .cover(dbBook.bookCoverImage)
		              .bookId(dbBook.bookId)
		              .authorId(dbBook.author.authorId).build();
	}
	
	private Book convertDtoToEntity(final BookDTO dto, final Author author) {
		if (dto != null) {
			return Book.builder()
			           .bookTitle(dto.getTitle())
			           .bookDescription(dto.getDescription())
			           .bookCoverImage(dto.getCover())
			           .bookPrice(dto.getPrice())
			           .author(author)
			           .build();
		}
		LOG.info("createBookEntity: Couldn't create Book entity");
		return null;
	}
	
	public boolean updateBook(final BookDTO dto) {
		return this.persister.update(dto);
	}
	
	public List<BookDTO> getBooksByAuthor(final Author author) {
		final Set<Book> dbBooks = author.getAuthorBooks();
		
		final List<BookDTO> authorBooks = new ArrayList<>();
		emptyIfNull(dbBooks).forEach(entity -> {
			final BookDTO dto = BookDTO.builder()
			                           .bookId(entity.bookId)
			                           .title(entity.bookTitle)
			                           .description(entity.bookDescription)
			                           .price(entity.bookPrice)
			                           .cover(entity.bookCoverImage)
			                           .authorId(author.authorId)
			                           .build();
			
			authorBooks.add(dto);
		});
		
		return authorBooks;
	}
	
	public Book getBook(final String id) {
		return find("book_id", id).firstResult();
	}
	
	public boolean deleteBook(final Book bookEntity) {
		try {
			this.persister.delete(bookEntity);
			return true;
		} catch (final Exception e) {
			LOG.error("deleteBook with id " + bookEntity.bookId + " Error {}", e.getMessage());
		}
		return false;
	}
	
	public boolean validateBookAuthor(final Book bookEntity, final String username) {
		final Author authenticatedUser = this.findUser(username);
		final String userId = authenticatedUser.authorId;
		
		final Author bookAuthor = bookEntity.author;
		final String authorId = bookAuthor.authorId;
		
		return userId.equals(authorId);
	}
	
}
