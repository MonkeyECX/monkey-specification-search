package br.com.monkey.ecx.specification;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
public class SearchCriteria implements Serializable {

	private String key;

	private SearchOperation operation;

	private String value;

	private Enum enumValue;

	public SearchCriteria(final String key, final String operation, final String value, String suffix) {
		SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
		if (op != null) {

			boolean endsWithAsterisk = suffix != null && suffix.contains(SearchOperation.LIKE);

			if (op.equals(SearchOperation.EQUAL) && endsWithAsterisk) {
				op = SearchOperation.STARTS_WITH;

			}
			else if (op.equals(SearchOperation.NOT) && endsWithAsterisk) {
				op = SearchOperation.DOES_NOT_START_WITH;
			}
		}

		this.key = key;
		this.operation = op;
		this.value = value;
	}

	public SearchCriteria changeKey(String key) {
		this.key = key;
		return this;
	}

	public SearchCriteria addEnumValue(Enum enumValue) {
		this.enumValue = enumValue;
		return this;
	}

}
