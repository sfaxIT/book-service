package de.sfaxit.util.validation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class PasswordRegexValidator implements ConstraintValidator<PasswordRegex, String> {
	
	private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
	
	@Override
	public void initialize(PasswordRegex constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}
	
	@Override
	public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
		if (password == null) {
			return false;
		}
		
		final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
		final Matcher matcher = pattern.matcher(password);
		
		return matcher.matches();
	}
	
}
