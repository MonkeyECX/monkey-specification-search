package br.com.monkey.ecx.specification;

import lombok.Getter;

@Getter
public class SearchCriteria {

	private String key;

	private SearchOperation operation;

	private String value;

	private Enum enumValue;

	public SearchCriteria(final String key, final String operation, String prefix,
			final String value, String suffix) {
		SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
		if (op != null) {

			boolean startsWithAsterisk = prefix != null
					&& prefix.contains(SearchOperation.LIKE);
			boolean endsWithAsterisk = suffix != null
					&& suffix.contains(SearchOperation.LIKE);

			if (op.equals(SearchOperation.EQUAL) && startsWithAsterisk
					&& endsWithAsterisk) {
				op = SearchOperation.CONTAINS;
			}
			else if (op.equals(SearchOperation.EQUAL) && startsWithAsterisk) {
				op = SearchOperation.ENDS_WITH;
			}
			else if (op.equals(SearchOperation.EQUAL) && endsWithAsterisk) {
				op = SearchOperation.STARTS_WITH;
			}

			if (op.equals(SearchOperation.NOT) && startsWithAsterisk
					&& endsWithAsterisk) {
				op = SearchOperation.DOES_NOT_CONTAIN;
			}
			else if (op.equals(SearchOperation.NOT) && startsWithAsterisk) {
				op = SearchOperation.DOES_NOT_END_WITH;
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
