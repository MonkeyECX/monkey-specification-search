package br.com.monkey.sdk.parser;

import br.com.monkey.sdk.core.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CriteriaParserRequiredKeysTest {

	private static final String EXTERNAL_ID = "externalId";

	private final CriteriaParser<Object> parser = new CriteriaParser<>();

	@ParameterizedTest
	@ValueSource(strings = { "externalId:1", "externalId:1 AND seller:2", "seller:2 AND externalId:1",
			"externalId:1 OR seller:2", "seller:2 AND (externalId:1 OR other:3)", "externalId!1", "externalId>1",
			"externalId<1", "externalId:1*", "externalId:*1*", "externalId:'1'", "externalId IN ['1', '2']" })
	void should_parse_when_the_search_filters_by_the_required_key(String search) {
		assertThat(parser.parse(search, EXTERNAL_ID)).isNotNull();
	}

	@ParameterizedTest
	@ValueSource(strings = { "seller:2", "seller:2 AND other:3", "(seller:2 OR other:3)", "seller IN ['1']",
			"seller:externalId" })
	void should_reject_when_the_search_does_not_filter_by_the_required_key(String search) {
		assertThatThrownBy(() -> parser.parse(search, EXTERNAL_ID)).isExactlyInstanceOf(BadRequestException.class)
			.hasMessageContaining(EXTERNAL_ID);
	}

	@Test
	void should_parse_nested_required_keys() {
		assertThat(parser.parse("seller.id:1 AND externalId:2", "seller.id", EXTERNAL_ID)).isNotNull();
	}

	@Test
	void should_report_every_missing_required_key() {
		assertThatThrownBy(() -> parser.parse("other:1", EXTERNAL_ID, "seller.id"))
			.isExactlyInstanceOf(BadRequestException.class)
			.satisfies(exception -> assertThat(((BadRequestException) exception).getNotifications())
				.containsExactly("Search parameter must filter by: externalId, seller.id"));
	}

	@Test
	void should_parse_any_search_when_there_are_no_required_keys() {
		assertThat(parser.parse("seller:2", new String[0])).isNotNull();
	}

}
