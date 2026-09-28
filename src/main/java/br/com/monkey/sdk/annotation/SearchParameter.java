package br.com.monkey.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchParameter {

	String value() default "search";

	/**
	 * Keys the search must contain, with any operator; otherwise the request fails with a
	 * {@link br.com.monkey.sdk.core.exception.BadRequestException}.
	 */
	String[] required() default {};

}
