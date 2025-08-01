package br.com.monkey.sdk.specification;

import br.com.monkey.sdk.parser.CriteriaParser;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationsBuilder<T> {

	private Specification<T> specifications = null;

	private CriteriaParser<T> parser = new CriteriaParser<>();

	public SpecificationsBuilder<T> withSearch(String search) {
		specifications = parser.parse(search);
		return this;
	}

	public Specification<T> build() {
		return specifications;
	}

}
