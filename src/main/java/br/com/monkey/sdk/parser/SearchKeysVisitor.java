package br.com.monkey.sdk.parser;

import br.com.monkey.sdk.QueryBaseVisitor;
import br.com.monkey.sdk.QueryParser;

import java.util.HashSet;
import java.util.Set;

/**
 * Collects every key a search filters by, whatever the operator or logical operation.
 */
class SearchKeysVisitor extends QueryBaseVisitor<Set<String>> {

	@Override
	public Set<String> visitInput(QueryParser.InputContext ctx) {
		return visit(ctx.query());
	}

	@Override
	public Set<String> visitAtomQuery(QueryParser.AtomQueryContext ctx) {
		return visit(ctx.criteria());
	}

	@Override
	public Set<String> visitPriorityQuery(QueryParser.PriorityQueryContext ctx) {
		return visit(ctx.query());
	}

	@Override
	public Set<String> visitOpQuery(QueryParser.OpQueryContext ctx) {
		Set<String> keys = new HashSet<>(visit(ctx.left));
		keys.addAll(visit(ctx.right));
		return keys;
	}

	@Override
	public Set<String> visitOpCriteria(QueryParser.OpCriteriaContext ctx) {
		return Set.of(ctx.key().getText());
	}

	@Override
	public Set<String> visitArrayCriteria(QueryParser.ArrayCriteriaContext ctx) {
		return Set.of(ctx.key().getText());
	}

}
