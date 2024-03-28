package de.sfaxit.util;

import de.sfaxit.model.dto.BookDTO;
import de.sfaxit.model.dto.enums.UserRole;
import de.sfaxit.model.entity.Author;
import de.sfaxit.model.entity.Book;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BookPersister {
	private static final Logger LOG = LoggerFactory.getLogger(BookPersister.class);
	
	@Transactional
	public Book addBook(@NotNull final Book bookToPersist, @NotNull final Long authorId) {
		try {
			final Author authorEntity = Author.findById(authorId);
			authorEntity.addBook(bookToPersist);
			
			bookToPersist.persist();
			LOG.debug("Attached book to author {}", bookToPersist);
			
			return bookToPersist;
		} catch (final Exception e) {
			LOG.error("Could not create a new book {} {}", e, e.getMessage());
		}
		return null;
	}
	
	@Transactional
	public boolean updateBook(final BookDTO dto) {
		final Long id = dto.getBookId();
		final Book entityToUpdate = Book.findById(id);
		
		if (entityToUpdate != null) {
			entityToUpdate.setTitle(dto.getTitle());
			entityToUpdate.setDescription(dto.getDescription());
			
			final BigDecimal bookPrice = dto.getPrice() != null ? this.computeBookPrice(dto.getPrice()) : null;
			entityToUpdate.setPrice(bookPrice);
			
			entityToUpdate.setCoverImage(dto.getCover());
			
			entityToUpdate.persist();
			return true;
		}
		return false;
	}
	
	private BigDecimal computeBookPrice(final String priceAsString) {
		final double price = Double.parseDouble(priceAsString);
		
		return BigDecimal.valueOf(price)
		                 .setScale(2, RoundingMode.HALF_UP);
		
	}
	
	@Transactional
	public void delete(final Book entity) {
		entity.delete();
	}
	
	@Transactional
	public List<Book> booksByTitle(final String title) {
		try (final Stream<Book> books = Book.streamAll()) {
			return books.filter(b -> StringUtils.equalsIgnoreCase(b.title, title))
			            .toList();
			
		} catch (final Exception e) {
			LOG.error("ERROR booksByTitle {}", e.getMessage(), e);
		}
		return null;
	}
	
	@Transactional
	public Author addUser(final String username, final String password, final UserRole authorRole) {
		final Author author = new Author(username, password);
		author.setAuthorRole(authorRole.name());
		author.setAuthorBooks(new HashSet<>());
		
		author.persist();
		LOG.info("Persisted user {}", author);
		
		return author;
	}
	
}
