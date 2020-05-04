package br.com.monkey.ecx.specification;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static br.com.monkey.ecx.core.DateUtils.*;
import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;

@AllArgsConstructor
public class SpecificationImpl<T> implements Specification<T> {

	private static final Pattern DATE = Pattern
			.compile("[0-9]{4}(/|-)[0-9]{1,2}(/|-)[0-9]{1,2}");

	private static final Pattern BOOLEAN = Pattern.compile("true|false");

	private final SearchCriteria criteria;

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery,
			CriteriaBuilder criteriaBuilder) {

		checkFieldInDepth(root);

		String[] nestedKey = criteria.getKey().split("\\.");
		Path nestedRoot = getNestedRoot(root, Arrays.asList(nestedKey));
		String criteriaKey = nestedKey[nestedKey.length - 1];

		if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
			if (isDate()) {
				Expression<String> dateStringExpr = criteriaBuilder.function("date",
						String.class, nestedRoot.get(criteriaKey));
				return criteriaBuilder.equal(criteriaBuilder.lower(dateStringExpr),
						formatDate(criteria.getValue()));
			}
			else if (isBoolean()) {
				return criteriaBuilder.equal(nestedRoot.get(criteriaKey),
						Boolean.valueOf(criteria.getValue()));
			}
			else {
				return criteriaBuilder.equal(nestedRoot.get(criteriaKey),
						criteria.getValue());
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.NOT)) {
			if (isDate()) {
				Expression<String> dateStringExpr = criteriaBuilder.function("date",
						String.class, nestedRoot.get(criteriaKey));
				return criteriaBuilder.notEqual(criteriaBuilder.lower(dateStringExpr),
						formatDate(criteria.getValue()));
			}
			else if (isBoolean()) {
				return criteriaBuilder.notEqual(nestedRoot.get(criteriaKey),
						Boolean.valueOf(criteria.getValue()));
			}
			else {
				return criteriaBuilder.notEqual(nestedRoot.get(criteriaKey),
						criteria.getValue());
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
			if (isDate()) {
				return criteriaBuilder.greaterThanOrEqualTo(
						nestedRoot.get(criteriaKey).as(Date.class),
						getStartOfDay(formatDate(criteria.getValue())).orElseThrow());
			}
			else {
				return criteriaBuilder.greaterThanOrEqualTo(nestedRoot.get(criteriaKey),
						Double.valueOf(criteria.getValue()));
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
			if (isDate()) {
				return criteriaBuilder.lessThanOrEqualTo(
						nestedRoot.get(criteriaKey).as(Date.class),
						getEndOfDay(formatDate(criteria.getValue())).orElseThrow());
			}
			else {
				return criteriaBuilder.lessThanOrEqualTo(nestedRoot.get(criteriaKey),
						Double.valueOf(criteria.getValue()));
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.STARTS_WITH)) {
			return criteriaBuilder.like(nestedRoot.get(criteriaKey),
					criteria.getValue() + "%");
		}
		else if (criteria.getOperation().equals(SearchOperation.ENDS_WITH)) {
			return criteriaBuilder.like(nestedRoot.get(criteriaKey),
					"%" + criteria.getValue());
		}
		else if (criteria.getOperation().equals(SearchOperation.CONTAINS)) {
			return criteriaBuilder.like(nestedRoot.get(criteriaKey),
					"%" + criteria.getValue() + "%");
		}
		else if (criteria.getOperation().equals(SearchOperation.DOES_NOT_START_WITH)) {
			return criteriaBuilder.notLike(nestedRoot.get(criteriaKey),
					criteria.getValue() + "%");
		}
		else if (criteria.getOperation().equals(SearchOperation.DOES_NOT_END_WITH)) {
			return criteriaBuilder.notLike(nestedRoot.get(criteriaKey),
					"%" + criteria.getValue());
		}
		else if (criteria.getOperation().equals(SearchOperation.DOES_NOT_CONTAIN)) {
			return criteriaBuilder.notLike(nestedRoot.get(criteriaKey),
					"%" + criteria.getValue() + "%");
		}
		else {
			return null;
		}

	}

	private void checkFieldInDepth(Root<T> root) {
		if (criteria.getKey().split("\\.").length == 1 && nonNull(root.getJavaType())) {
			if (stream(root.getJavaType().getDeclaredFields())
					.anyMatch(field -> field.getName().equals(criteria.getKey()))) {
				return;
			}
			stream(root.getJavaType().getDeclaredFields()).forEach(rootField -> {
				if (nonNull(rootField.getType().getPackageName()) && rootField.getType()
						.getPackageName().contains(root.getJavaType().getPackageName())) {
					stream(rootField.getType().getDeclaredFields())
							.filter(depthField -> depthField.getName()
									.equalsIgnoreCase(criteria.getKey()))
							.findFirst().ifPresent(field2 -> {
								String concat = rootField.getName().concat(".")
										.concat(field2.getName());
								criteria.changeKey(concat);
							});
				}
			});
		}
	}

	private Path getNestedRoot(Root<T> root, List<String> nestedKey) {
		ArrayList<String> prefix = new ArrayList<>(nestedKey);
		prefix.remove(nestedKey.size() - 1);
		Path temp = root;
		for (String p : prefix) {
			temp = temp.get(p);
		}
		return temp;
	}

	private boolean isDate() {
		return DATE.matcher(criteria.getValue()).matches();
	}

	private boolean isBoolean() {
		return BOOLEAN.matcher(criteria.getValue()).matches();
	}

}
