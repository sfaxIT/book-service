package de.sfaxit.service;

import static de.sfaxit.model.entity.Book.DATE_FORMAT;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import de.sfaxit.model.dto.BookDTO;
import de.sfaxit.model.entity.Book;
import de.sfaxit.model.entity.Author;
import de.sfaxit.model.dto.SearchResultHolderDTO;
import de.sfaxit.util.BookPersister;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BookService {
	private static final Logger LOG = LoggerFactory.getLogger(BookService.class);
	
	@Inject
	BookPersister persister;
	
	public boolean bookExists(Long id) {
		return Book.findById(id) != null;
	}
	
	public SearchResultHolderDTO findAllBooksByPage(final Integer pageSize) {
		// Restrict fetching the number of books based on given page size
		PanacheQuery<Book> booksPage = Book.findAll(Sort.by("bookId", Sort.Direction.Descending))
		                                   .page(Page.ofSize(pageSize));
		
		final List<Book> result = new ArrayList<>(booksPage.list());
		
		while (booksPage.hasNextPage()) {
			booksPage = booksPage.nextPage();
			
			result.addAll(booksPage.list());
		}
		LOG.info("Book result count {}", result.size());
		
		final List<BookDTO> books = new ArrayList<>();
		emptyIfNull(result).forEach(entity -> {
			final BookDTO dto = this.mapBookEntityToDto(entity);
			books.add(dto);
		});
		LOG.info("BookDTO count {}", books.size());
		
		return SearchResultHolderDTO.builder()
		                            .currentPage(booksPage.page().index)
		                            .pageCount(booksPage.pageCount())
		                            .pageSize(booksPage.page().size)
		                            .totalCount(booksPage.count())
		                            .books(books)
		                            .build();
	}
	
	public SearchResultHolderDTO findBooksBySearchTerm(final Integer pageSize, final String searchByTerm, final String termValue) {
		final PanacheQuery<Book> booksBySearchTerm = Book.findByTitle(searchByTerm, termValue, pageSize);
		
		if (booksBySearchTerm != null) {
			final List<Book> result = booksBySearchTerm.list();
			LOG.info("BooksBySearchTerm result count {}", result.size());
			
			final List<BookDTO> books = new ArrayList<>();
			emptyIfNull(result).forEach(entity -> {
				final BookDTO dto = this.mapBookEntityToDto(entity);
				books.add(dto);
			});
			LOG.info("BookDTO result count {}", books.size());
			
			return SearchResultHolderDTO.builder()
			                            .currentPage(booksBySearchTerm.page().index)
			                            .pageCount(booksBySearchTerm.pageCount())
			                            .pageSize(booksBySearchTerm.page().size)
			                            .totalCount(booksBySearchTerm.count())
			                            .books(books)
			                            .build();
		}
		return null;
	}
	
	public List<BookDTO> findAllBooksByTitle(final String title) {
		final List<Book> result = this.persister.booksByTitle(title);
		
		if (result != null) {
			final List<BookDTO> books = new ArrayList<>();
			emptyIfNull(result).forEach(entity -> {
				final BookDTO dto = this.mapBookEntityToDto(entity);
				books.add(dto);
			});
			return books;
		}
		return null;
	}
	
	public BookDTO publishBook(final BookDTO dtoToPersist) {
		final Book bookEntityToPersist = this.mapDtoToEntity(dtoToPersist);

		final Book dbBook = this.persister.addBook(bookEntityToPersist, dtoToPersist.getAuthorId());

		return this.mapBookEntityToDto(dbBook);
	}
	
	private String formatPublishDate(final Date date) {
		if (date != null) {
			final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			return format.format(date);
		}
		return null;
	}
	
	private Book  mapDtoToEntity(final BookDTO dto) {
		if (dto != null) {
			final BigDecimal bookPrice = dto.getPrice() != null ? this.computeBookPrice(dto.getPrice()) : null;
			
			final Book book = new Book();
			book.setTitle(dto.getTitle());
			book.setDescription(dto.getDescription());
			book.setPrice(bookPrice);
			book.setCoverImage(dto.getCover());
			
			final String publishDate = this.formatPublishDate(new Date());
			book.setPublishDate(publishDate);
			
			LOG.info("convertDtoToEntity {}", book);
			return book;
		}
		LOG.info("mapDtoToEntity: Couldn't create Book entity");
		return null;
	}
	
	private BookDTO mapBookEntityToDto(final Book entity) {
		if (entity != null) {
			return BookDTO.builder()
			              .bookId(entity.bookId)
			              .authorId(entity.author.authorId)
			              .title(entity.title)
			              .description(entity.description)
			              .price(entity.price.toString())
			              .publishDate(entity.publishDate)
			              .cover(entity.coverImage)
			              .build();
		}
		LOG.info("mapBookEntityToDto: Couldn't create Book entity");
		return null;
	}
	
	private BigDecimal computeBookPrice(final String priceAsString) {
		final double price = Double.parseDouble(priceAsString);
		
		return BigDecimal.valueOf(price)
		                 .setScale(2, RoundingMode.HALF_UP);

	}
	
	public boolean updateBook(final BookDTO dto) {
		return this.persister.updateBook(dto);
	}
	
	public List<BookDTO> getBooksByAuthor(final Author author) {
		final Set<Book> dbBooks = author.getAuthorBooks();
		
		final List<BookDTO> authorBooks = new ArrayList<>();
		
		emptyIfNull(dbBooks).forEach(entity -> {
			final BookDTO dto = this.mapBookEntityToDto(entity);
			
			authorBooks.add(dto);
		});
		
		return authorBooks;
	}
	
	public Book getBook(final Long id) {
		return Book.findById(id);
	}
	
	public boolean deleteBook(final Long bookId) {
		try {
			final Book bookToDelete = Book.findById(bookId);
			if (bookToDelete.isPersistent()) {
				this.persister.delete(bookToDelete);
				return true;
			}
		} catch (final Exception e) {
			LOG.error("Error deleteBook with id " + bookId + " {}", e.getMessage());
		}
		return false;
	}
	
}
