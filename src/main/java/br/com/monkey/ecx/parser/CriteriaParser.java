package br.com.monkey.ecx.parser;

import br.com.monkey.ecx.QueryLexer;
import br.com.monkey.ecx.QueryParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.data.jpa.domain.Specification;

public class CriteriaParser<T> {

	private QueryVisitor<T> visitor = new QueryVisitor<>();

	public Specification<T> parse(String search) {
		QueryParser parser = getParser(search);
		return visitor.visit(parser.input());
	}

	private QueryParser getParser(String search) {
		QueryLexer lexer = new QueryLexer(CharStreams.fromString(search));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		return new QueryParser(tokens);
	}

}
