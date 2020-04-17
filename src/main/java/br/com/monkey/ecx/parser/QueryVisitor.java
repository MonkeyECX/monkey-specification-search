package br.com.monkey.ecx.parser;

import br.com.monkey.ecx.QueryBaseVisitor;
import br.com.monkey.ecx.QueryParser;
import br.com.monkey.ecx.specification.SearchCriteria;
import br.com.monkey.ecx.specification.SearchOperation;
import br.com.monkey.ecx.specification.SpecificationImpl;
import org.springframework.data.jpa.domain.Specification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

class QueryVisitor<T> extends QueryBaseVisitor<Specification<T>> {

	private Pattern REGEX = Pattern.compile("^(\\*?)(.+?)(\\*?)$");

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

		if (op.equalsIgnoreCase(SearchOperation.AND_OPERATOR)) {
			return left.and(right);
		}
		else if (op.equalsIgnoreCase(SearchOperation.OR_OPERATOR)) {
			return left.or(right);
		}
		else {
			return left.and(right);
		}
	}

	@Override
	public Specification<T> visitCriteria(QueryParser.CriteriaContext ctx) {
		String key = ctx.key().getText();
		String op = ctx.op().getText();
		String value = ctx.value().getText();

		if (nonNull(ctx.value().STRING())) {
			value = value.replace("'", "").replace("\"", "").replace("\\\"", "\"")
					.replace("\\'", "'");
		}
		Matcher matchResult = REGEX.matcher(value);
		SearchCriteria criteria;
		if (matchResult.matches()) {
			criteria = new SearchCriteria(key, op, matchResult.group(1),
					matchResult.group(2), matchResult.group(3));
		}
		else {
			criteria = new SearchCriteria(key, op, null, matchResult.group(2), null);
		}
		return new SpecificationImpl<>(criteria);
	}

}
