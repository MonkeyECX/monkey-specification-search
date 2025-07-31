package br.com.monkey.sdk.specification;

import br.com.monkey.sdk.core.exception.BadRequestException;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.ListAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.query.sqm.tree.domain.SqmPluralValuedSimplePath;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static br.com.monkey.sdk.core.MonkeySpecificationSearchDateUtils.*;
import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class SpecificationImpl<T> implements Specification<T> {

	private static final Pattern DATE = Pattern.compile("[0-9]{4}(/|-)[0-9]{1,2}(/|-)[0-9]{1,2}");

	private static final Pattern BOOLEAN = Pattern.compile("true|false");

	@Getter
	private SearchCriteria criteria;

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

		checkFieldInDepth(root);

		String[] nestedKey = criteria.getKey().split("\\.");
		Path nestedRoot = getNestedRoot(root, Arrays.asList(nestedKey));
		String criteriaKey = nestedKey[nestedKey.length - 1];

		if (!isList(nestedRoot) && nestedRoot.get(criteriaKey).getJavaType().isEnum()) {
			Enum<?>[] enumConstants = (Enum<?>[]) nestedRoot.get(criteriaKey).getJavaType().getEnumConstants();
			Enum<?> value = stream(enumConstants).filter(e -> e.name().equals(criteria.getValue()))
				.findFirst()
				.orElseThrow(() -> new BadRequestException("enum.value.not.found"));
			criteria.addEnumValue(value);
		}

		if (criteria.getOperation().equals(SearchOperation.IN_ARRAY)) {
			if (isList(nestedRoot)) {
				criteriaQuery.distinct(true);
				return criteriaBuilder
					.in(root.join(((SqmPluralValuedSimplePath) nestedRoot).getNavigablePath().getLocalName())
						.get(criteriaKey))
					.value(criteria.getValues());
			}
			else {
				return criteriaBuilder.in(nestedRoot.get(criteriaKey)).value(criteria.getValues());
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.NOT_IN_ARRAY)) {
			if (isList(nestedRoot)) {
				criteriaQuery.distinct(true);
				return criteriaBuilder.not(
						criteriaBuilder
							.in(root.join(((SqmPluralValuedSimplePath) nestedRoot).getNavigablePath().getLocalName())
								.get(criteriaKey))
							.value(criteria.getValues()));
			}
			else {
				return criteriaBuilder.not(criteriaBuilder.in(nestedRoot.get(criteriaKey)).value(criteria.getValues()));
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
			if (isDate()) {
				Expression<String> dateStringExpr = criteriaBuilder.function("date", String.class,
						nestedRoot.get(criteriaKey));
				return criteriaBuilder.equal(dateStringExpr, criteria.getValue());
			}
			else if (nonNull(criteria.getEnumValue())) {
				return criteriaBuilder.equal(nestedRoot.get(criteriaKey), criteria.getEnumValue());
			}
			else if (isBoolean()) {
				return criteriaBuilder.equal(nestedRoot.get(criteriaKey), Boolean.valueOf(criteria.getValue()));
			}
			else if (isList(nestedRoot)) {
				criteriaQuery.distinct(true);
				return criteriaBuilder
					.equal(root.join(((SqmPluralValuedSimplePath) nestedRoot).getNavigablePath().getLocalName())
						.get(criteriaKey), criteria.getValue());
			}
			else {
				return criteriaBuilder.equal(nestedRoot.get(criteriaKey), criteria.getValue());
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.NOT)) {
			if (isDate()) {
				Expression<String> dateStringExpr = criteriaBuilder.function("date", String.class,
						nestedRoot.get(criteriaKey));
				return criteriaBuilder.notEqual(dateStringExpr, criteria.getValue());
			}
			else if (nonNull(criteria.getEnumValue())) {
				return criteriaBuilder.notEqual(nestedRoot.get(criteriaKey), criteria.getEnumValue());
			}
			else if (isBoolean()) {
				return criteriaBuilder.notEqual(nestedRoot.get(criteriaKey), Boolean.valueOf(criteria.getValue()));
			}
			else if (isList(nestedRoot)) {
				criteriaQuery.distinct(true);
				return criteriaBuilder
					.notEqual(root.join(((SqmPluralValuedSimplePath) nestedRoot).getNavigablePath().getLocalName())
						.get(criteriaKey), criteria.getValue());
			}
			else {
				return criteriaBuilder.notEqual(nestedRoot.get(criteriaKey), criteria.getValue());
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
			if (isDate()) {
				return criteriaBuilder.greaterThanOrEqualTo(nestedRoot.get(criteriaKey).as(Date.class),
						getStartOfDay(formatDate(criteria.getValue())).orElseThrow());
			}
			else if (nonNull(criteria.getEnumValue())) {
				throw new BadRequestException("enum.not.valid.for.operation");
			}
			else if (isList(nestedRoot)) {
				criteriaQuery.distinct(true);
				return criteriaBuilder.greaterThanOrEqualTo(
						root.join(((SqmPluralValuedSimplePath) nestedRoot).getNavigablePath().getLocalName())
							.get(criteriaKey),
						criteria.getValue());
			}
			else {
				return criteriaBuilder.greaterThanOrEqualTo(nestedRoot.get(criteriaKey),
						Double.valueOf(criteria.getValue()));
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
			if (isDate()) {
				return criteriaBuilder.lessThanOrEqualTo(nestedRoot.get(criteriaKey).as(Date.class),
						getEndOfDay(formatDate(criteria.getValue())).orElseThrow());
			}
			else if (nonNull(criteria.getEnumValue())) {
				throw new BadRequestException("enum.not.valid.for.operation");
			}
			else if (isList(nestedRoot)) {
				criteriaQuery.distinct(true);
				return criteriaBuilder.lessThanOrEqualTo(
						root.join(((SqmPluralValuedSimplePath) nestedRoot).getNavigablePath().getLocalName())
							.get(criteriaKey),
						criteria.getValue());
			}
			else {
				return criteriaBuilder.lessThanOrEqualTo(nestedRoot.get(criteriaKey),
						Double.valueOf(criteria.getValue()));
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.STARTS_WITH)) {
			if (nonNull(criteria.getEnumValue())) {
				throw new BadRequestException("enum.not.valid.for.operation");
			}
			else if (isList(nestedRoot)) {
				criteriaQuery.distinct(true);
				return criteriaBuilder.like(criteriaBuilder
					.lower(root.join(((SqmPluralValuedSimplePath) nestedRoot).getNavigablePath().getLocalName())
						.get(criteriaKey)), criteria.getValue().toLowerCase() + "%");
			}
			else {
				return criteriaBuilder.like(criteriaBuilder.lower(nestedRoot.get(criteriaKey)),
						criteria.getValue().toLowerCase() + "%");
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.ENDS_WITH)) {
			if (nonNull(criteria.getEnumValue())) {
				throw new BadRequestException("enum.not.valid.for.operation");
			}
			else if (isList(nestedRoot)) {
				criteriaQuery.distinct(true);
				return criteriaBuilder.like(criteriaBuilder
					.lower(root.join(((SqmPluralValuedSimplePath) nestedRoot).getNavigablePath().getLocalName())
						.get(criteriaKey)), "%" + criteria.getValue().toLowerCase());
			}
			else {
				return criteriaBuilder.like(criteriaBuilder.lower(nestedRoot.get(criteriaKey)),
						"%" + criteria.getValue().toLowerCase());
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.CONTAINS)) {
			if (nonNull(criteria.getEnumValue())) {
				throw new BadRequestException("enum.not.valid.for.operation");
			}
			else if (isList(nestedRoot)) {
				criteriaQuery.distinct(true);
				return criteriaBuilder.like(criteriaBuilder
					.lower(root.join(((SqmPluralValuedSimplePath) nestedRoot).getNavigablePath().getLocalName())
						.get(criteriaKey)), "%" + criteria.getValue().toLowerCase() + "%");
			}
			else {
				return criteriaBuilder.like(criteriaBuilder.lower(nestedRoot.get(criteriaKey)),
						"%" + criteria.getValue().toLowerCase() + "%");
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.DOES_NOT_START_WITH)) {
			if (nonNull(criteria.getEnumValue())) {
				throw new BadRequestException("enum.not.valid.for.operation");
			}
			else if (isList(nestedRoot)) {
				criteriaQuery.distinct(true);
				return criteriaBuilder.notLike(criteriaBuilder
					.lower(root.join(((SqmPluralValuedSimplePath) nestedRoot).getNavigablePath().getLocalName())
						.get(criteriaKey)), criteria.getValue().toLowerCase() + "%");
			}
			else {
				return criteriaBuilder.notLike(criteriaBuilder.lower(nestedRoot.get(criteriaKey)),
						criteria.getValue().toLowerCase() + "%");
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.DOES_NOT_END_WITH)) {
			if (nonNull(criteria.getEnumValue())) {
				throw new BadRequestException("enum.not.valid.for.operation");
			}
			else if (isList(nestedRoot)) {
				criteriaQuery.distinct(true);
				return criteriaBuilder.notLike(criteriaBuilder
					.lower(root.join(((SqmPluralValuedSimplePath) nestedRoot).getNavigablePath().getLocalName())
						.get(criteriaKey)), "%" + criteria.getValue().toLowerCase());
			}
			else {
				return criteriaBuilder.notLike(criteriaBuilder.lower(nestedRoot.get(criteriaKey)),
						"%" + criteria.getValue().toLowerCase());
			}
		}
		else if (criteria.getOperation().equals(SearchOperation.DOES_NOT_CONTAIN)) {
			if (nonNull(criteria.getEnumValue())) {
				throw new BadRequestException("enum.not.valid.for.operation");
			}
			else if (isList(nestedRoot)) {
				criteriaQuery.distinct(true);
				return criteriaBuilder.notLike(criteriaBuilder
					.lower(root.join(((SqmPluralValuedSimplePath) nestedRoot).getNavigablePath().getLocalName())
						.get(criteriaKey)), "%" + criteria.getValue().toLowerCase() + "%");
			}
			else {
				return criteriaBuilder.notLike(criteriaBuilder.lower(nestedRoot.get(criteriaKey)),
						"%" + criteria.getValue().toLowerCase() + "%");
			}
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
				if (nonNull(rootField.getType().getPackageName())
						&& rootField.getType().getPackageName().contains(root.getJavaType().getPackageName())) {
					stream(rootField.getType().getDeclaredFields())
						.filter(depthField -> depthField.getName().equalsIgnoreCase(criteria.getKey()))
						.findFirst()
						.ifPresent(field2 -> {
							String concat = rootField.getName().concat(".").concat(field2.getName());
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

	public boolean isList(Path nestedRoot) {
		if (nestedRoot instanceof SqmPluralValuedSimplePath) {
			return ((SqmPluralValuedSimplePath<?>) nestedRoot).getExpressible() instanceof ListAttribute;
		}
		return false;
	}

}
