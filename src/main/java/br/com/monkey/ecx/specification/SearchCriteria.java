package br.com.monkey.ecx.specification;

import lombok.Getter;

@Getter
public class SearchCriteria {

	private String key;

	private SearchOperation operation;

	private String value;

	public SearchCriteria(final String key, final String operation, String prefix,
			final String value, String suffix) {
		SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
		if (op != null) {

			boolean startsWithAsterisk = prefix != null
					&& prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
			boolean endsWithAsterisk = suffix != null
					&& suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

			if (op.equals(SearchOperation.EQUALITY) && startsWithAsterisk
					&& endsWithAsterisk) {
				op = SearchOperation.CONTAINS;
			}
			else if (op.equals(SearchOperation.EQUALITY) && startsWithAsterisk) {
				op = SearchOperation.ENDS_WITH;
			}
			else if (op.equals(SearchOperation.EQUALITY) && endsWithAsterisk) {
				op = SearchOperation.STARTS_WITH;
			}

			if (op.equals(SearchOperation.NEGATION) && startsWithAsterisk
					&& endsWithAsterisk) {
				op = SearchOperation.DOESNT_CONTAIN;
			}
			else if (op.equals(SearchOperation.NEGATION) && startsWithAsterisk) {
				op = SearchOperation.DOESNT_END_WITH;
			}
			else if (op.equals(SearchOperation.NEGATION) && endsWithAsterisk) {
				op = SearchOperation.DOESNT_START_WITH;
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

}
