package de.sfaxit.util;

import de.sfaxit.model.dto.BookDTO;
import de.sfaxit.model.entity.Book;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

@ApplicationScoped
public class DtoToEntityConverter {
	private static final Logger LOG = LoggerFactory.getLogger(DtoToEntityConverter.class);
	
	public Book createBookEntity(final BookDTO dto) {
		if (dto != null) {
			final BigDecimal bookPrice = dto.getPrice() != null ? this.computeBookPrice(dto.getPrice()) : null;
			
			final Book book = new Book();
			return book;
/*			return Book.builder()
			           .title(dto.getTitle())
			           .description(dto.getDescription())
			           .coverImage(dto.getCover())
			           .price(bookPrice)
			           .build();*/
		}
		LOG.info("createBookEntity: Couldn't create Book entity");
		return null;
	}
	
	private BigDecimal computeBookPrice(final String priceAsString) {
		final double price = Double.parseDouble(priceAsString);
		
		return BigDecimal.valueOf(price)
		                 .setScale(2, RoundingMode.HALF_UP);
		
	}
	
}
