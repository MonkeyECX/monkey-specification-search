package br.com.monkey.ecx.configuration;

import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;
import java.util.Map;

public class CustomSpecificationSearch {

	public static final String CUSTOM_PREDICATE = "customPredicate";

	private static CustomSpecificationSearch INSTANCE = null;

	private Map<String, Specification> customPredicates;

	private CustomSpecificationSearch() {
		// NOT TO DO
	}

	public static CustomSpecificationSearch getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CustomSpecificationSearch();
		}
		return INSTANCE;
	}

	public CustomSpecificationSearch add(String name, Specification predicate) {
		if (customPredicates == null) {
			customPredicates = new HashMap<>();
		}
		customPredicates.put(name, predicate);
		return this;
	}

	public Specification getPredicate(String name) {
		return customPredicates.getOrDefault(name, null);
	}

}
