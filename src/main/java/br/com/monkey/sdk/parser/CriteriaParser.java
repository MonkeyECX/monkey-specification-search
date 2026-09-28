package br.com.monkey.sdk.parser;

import br.com.monkey.sdk.QueryLexer;
import br.com.monkey.sdk.QueryParser;
import br.com.monkey.sdk.core.exception.BadRequestException;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CriteriaParser<T> {

	private static final String MISSING_REQUIRED_KEYS = "Search parameter must filter by: ";

	private QueryVisitor<T> visitor = new QueryVisitor<>();

	public Specification<T> parse(String search) {
		QueryParser parser = getParser(search);
		return visitor.visit(parser.input());
	}

	public Specification<T> parse(String search, String... requiredKeys) {
		QueryParser.InputContext input = getParser(search).input();
		Set<String> missingKeys = new LinkedHashSet<>(List.of(requiredKeys));
		missingKeys.removeAll(new SearchKeysVisitor().visit(input));
		if (!missingKeys.isEmpty()) {
			throw missingRequiredKeys(missingKeys);
		}
		return visitor.visit(input);
	}

	public static BadRequestException missingRequiredKeys(Collection<String> keys) {
		return new BadRequestException(MISSING_REQUIRED_KEYS + String.join(", ", keys));
	}

	private QueryParser getParser(String search) {
		QueryLexer lexer = new QueryLexer(CharStreams.fromString(search));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new QueryParser(tokens);
	}

}
