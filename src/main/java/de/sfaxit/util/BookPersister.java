package de.sfaxit.util;

import de.sfaxit.model.dto.BookDTO;
import de.sfaxit.model.entity.Subscriber;
import de.sfaxit.model.entity.Book;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

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
	public Book addBook(@NotNull final Book bookToPersist, @NotNull final Long subscriberId) {
		try {
			final Subscriber subscriberEntity = Subscriber.findById(subscriberId);
			subscriberEntity.addBook(bookToPersist);
			
			bookToPersist.persist();
			LOG.debug("Attached book to subscriber {}", bookToPersist);
			
			return bookToPersist;
		} catch (final Exception e) {
			LOG.error("Could not create a new book {} {}", e, e.getMessage());
		}
		return null;
	}
	
	@Transactional
	public boolean updateBook(final BookDTO dto) {
		try {
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
		} catch (final Exception e) {
			LOG.error("ERROR updateBook {}", e.getMessage(), e);
		}
		return false;
	}
	
	private BigDecimal computeBookPrice(final String priceAsString) {
		final double price = Double.parseDouble(priceAsString);
		
		return BigDecimal.valueOf(price)
		                 .setScale(2, RoundingMode.HALF_UP);
		
	}
	
	@Transactional
	public boolean delete(final Long bookId) {
		try {
			final Book bookToDelete = Book.findById(bookId);
			if (bookToDelete.isPersistent()) {
				bookToDelete.delete();
				return true;
			}
		} catch (final Exception e) {
			LOG.error("Error delete book with id " + bookId + " {}", e.getMessage());
		}
		return false;
	}
	
	@Transactional
	public List<Book> booksByTitle(final String bookTitle) {
		try (final Stream<Book> books = Book.streamAll()) {
			return books.filter(b -> StringUtils.equalsIgnoreCase(b.title, bookTitle))
			            .toList();
			
		} catch (final Exception e) {
			LOG.error("ERROR booksByTitle {}", e.getMessage(), e);
		}
		return null;
	}
	
}
