package br.com.monkey.sdk.specification;

public enum SearchOperation {

	EQUAL, NOT, GREATER_THAN_EQUAL, LESS_THAN_EQUAL, STARTS_WITH, ENDS_WITH,

	CONTAINS, DOES_NOT_START_WITH, DOES_NOT_END_WITH, DOES_NOT_CONTAIN, IN_ARRAY, NOT_IN_ARRAY;

	public static final String LIKE = "*";

	public static final String OR = "OR";

	public static final String AND = "AND";

	public static SearchOperation getSimpleOperation(final char input) {
		switch (input) {
			case ':':
				return EQUAL;
			case '!':
				return NOT;
			case '>':
				return GREATER_THAN_EQUAL;
			case '<':
				return LESS_THAN_EQUAL;
			default:
				return null;
		}
	}

}
