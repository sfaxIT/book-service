package de.sfaxit.util;

import de.sfaxit.model.dto.BookDTO;
import de.sfaxit.model.entity.Book;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DtoToEntityConverter {
	private static final Logger LOG = LoggerFactory.getLogger(DtoToEntityConverter.class);
	
	public Book createBookEntity(final BookDTO dto) {
		if (dto != null) {
			return Book.builder()
			           .bookTitle(dto.getTitle())
			           .bookDescription(dto.getDescription())
			           .bookCoverImage(dto.getCover())
			           .bookPrice(dto.getPrice())
			           .build();
		}
		LOG.info("createBookEntity: Couldn't create Book entity");
		return null;
	}
	
}
