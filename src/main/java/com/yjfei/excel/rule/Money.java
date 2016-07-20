package com.yjfei.excel.rule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { MoneyValidator.class })
public @interface Money {
	String message() default "涓嶆槸閲戦褰㈠紡";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}