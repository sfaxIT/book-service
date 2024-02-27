package de.sfaxit.util;

import de.sfaxit.model.dto.BookDTO;
import de.sfaxit.model.entity.Author;
import de.sfaxit.model.entity.Book;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BookPersister {
	private static final Logger LOG = LoggerFactory.getLogger(BookPersister.class);
	
	@Transactional
	public Book create(final Book entity) {
		try {
			if (entity != null) {
				entity.persist();
				LOG.debug("Created a new book {}", entity.bookId);
				
				return entity;
			}
		} catch (final Exception e) {
			LOG.error("Could not create a new book {} {}", e, e.getMessage());
		}
		return null;
	}
	
	@Transactional
	public boolean update(final BookDTO dto) {
		final String id = dto.getBookId();
		final Book entityToUpdate = Book.findById(id);
		
		if (entityToUpdate != null) {
			entityToUpdate.setBookTitle(dto.getTitle());
			entityToUpdate.setBookDescription(dto.getDescription());
			entityToUpdate.setBookPrice(dto.getPrice());
			entityToUpdate.setBookCoverImage(dto.getCover());
			
			final Author authorToUpdate = Author.findById(dto.getAuthorId());
			if (authorToUpdate == null) {
				return false;
			} else {
				authorToUpdate.addBook(entityToUpdate);
				entityToUpdate.setAuthor(authorToUpdate);
			}
			
			entityToUpdate.persist();
			return true;
		}
		return false;
	}
	
	@Transactional
	public void delete(final Book entity) {
		entity.delete();
	}
	
}
