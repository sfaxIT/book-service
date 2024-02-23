package de.sfaxit.util.validation;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookRegExValidator implements ConstraintValidator<BookRegEx, String> {
    private List<String> patterns = new ArrayList<>();

    @Override
    public void initialize(final BookRegEx constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);

        this.patterns = Arrays.asList("bookTitle", "bookDescription", "bookPrice", "author");
    }

    @Override
    public boolean isValid(final String regex, final ConstraintValidatorContext constraintValidatorContext) {
        if (regex == null || isEmpty(regex)) {
            return false;
        }


        return this.patterns.contains(regex);

/*        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher("bookTitle, bookDescription, bookPrice, author");

        return matcher.find();*/
    }

}
