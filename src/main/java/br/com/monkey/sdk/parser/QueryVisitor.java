package br.com.monkey.sdk.parser;

import br.com.monkey.sdk.QueryBaseVisitor;
import br.com.monkey.sdk.QueryParser;
import br.com.monkey.sdk.configuration.CustomSpecificationSearch;
import br.com.monkey.sdk.core.exception.BadRequestException;
import br.com.monkey.sdk.specification.SearchCriteria;
import br.com.monkey.sdk.specification.SearchOperation;
import br.com.monkey.sdk.specification.SpecificationImpl;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static br.com.monkey.sdk.configuration.CustomSpecificationSearch.CUSTOM_PREDICATE;
import static java.util.Objects.nonNull;

class QueryVisitor<T> extends QueryBaseVisitor<Specification<T>> {

	private final Pattern REGEX = Pattern.compile("^(\\*?)(.+?)(\\*?)$");

	@Override
	public Specification<T> visitInput(QueryParser.InputContext ctx) {
		return visit(ctx.query());
	}

	@Override
	public Specification<T> visitAtomQuery(QueryParser.AtomQueryContext ctx) {
		return visit(ctx.criteria());
	}

	@Override
	public Specification<T> visitPriorityQuery(QueryParser.PriorityQueryContext ctx) {
		return visit(ctx.query());
	}

	@Override
	public Specification<T> visitOpQuery(QueryParser.OpQueryContext ctx) {
		Specification<T> left = visit(ctx.left);
		Specification<T> right = visit(ctx.right);
		String op = ctx.logicalOp.getText();

		if (op.equalsIgnoreCase(SearchOperation.AND)) {
			return left.and(right);
		}
		else if (op.equalsIgnoreCase(SearchOperation.OR)) {
			return left.or(right);
		}
		else {
			return left.and(right);
		}
	}

	@Override
	public Specification<T> visitOpCriteria(QueryParser.OpCriteriaContext ctx) {
		String key = ctx.key().getText();
		String op = ctx.op().getText();
		String value = ctx.value().getText();

		if (key.startsWith(CUSTOM_PREDICATE)) {
			Specification<T> specification = CustomSpecificationSearch.getInstance().getPredicate(value);
			if (specification == null) {
				throw new BadRequestException("Custom predicate: " + value + " not found!");
			}
			return specification;
		}

		if (nonNull(ctx.value().STRING())) {
			value = value.replace("'", "").replace("\"", "").replace("\\\"", "\"").replace("\\'", "'");
		}
		Matcher matchResult = REGEX.matcher(value);
		SearchCriteria criteria;
		if (matchResult.matches()) {
			criteria = new SearchCriteria(key, op, matchResult.group(1), matchResult.group(2), matchResult.group(3));
		}
		else {
			criteria = new SearchCriteria(key, op, null, matchResult.group(2), null);
		}
		return new SpecificationImpl<>(criteria);
	}

	@Override
	public Specification<T> visitArrayCriteria(QueryParser.ArrayCriteriaContext ctx) {
		String key = ctx.key().getText();
		SearchOperation op = SearchOperation.IN_ARRAY;

		QueryParser.ArrayContext arr = ctx.array();
		List<QueryParser.ValueContext> arrayValues = arr.value();

		List<String> valueAsList = arrayValues.stream()
			.map(value -> value.STRING() != null ? clearString(value.getText()) : value.getText())
			.toList();

		SearchCriteria criteria = new SearchCriteria(key, op, valueAsList);
		return new SpecificationImpl<>(criteria);
	}

	private String clearString(String value) {
		if ((value.startsWith("'") && value.endsWith("'")) || (value.startsWith("\"") && value.endsWith("\""))) {
			value = value.substring(1, value.length() - 1);
		}
		return value.replace("\\\"", "\"").replace("\\'", "'");
	}

}
