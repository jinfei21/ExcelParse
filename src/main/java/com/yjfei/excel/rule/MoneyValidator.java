package com.yjfei.excel.rule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MoneyValidator implements ConstraintValidator<Money, CharSequence> {
	private String message;
	private Pattern pattern = Pattern.compile("^a*");

	@Override
	public void initialize(Money constraintAnnotation) {
		this.message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}
}