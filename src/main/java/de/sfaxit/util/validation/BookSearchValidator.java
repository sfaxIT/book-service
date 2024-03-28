package de.sfaxit.util.validation;

import de.sfaxit.model.dto.enums.SearchByTerm;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
public class BookSearchValidator implements ConstraintValidator<ValidBookSearch, String> {
	
	@Override
	public boolean isValid(final String givenTerm, final ConstraintValidatorContext context) {
		if (givenTerm == null) {
			return false;
		}
		
		final List<String> patterns = Stream.of(SearchByTerm.values())
		                                    .map(Enum::name)
		                                    .toList();
		
		return patterns.contains(givenTerm.toUpperCase());
	}
	
}
