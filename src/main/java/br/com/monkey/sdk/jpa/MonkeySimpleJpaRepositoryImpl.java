package br.com.monkey.sdk.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.jpa.support.PageableUtils;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Optional;

public class MonkeySimpleJpaRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {

	private final EntityManager entityManager;

	public MonkeySimpleJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	@Override
	public Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable) {
		if (hasNoGroupBy(spec)) {
			return super.findAll(spec, pageable);
		}

		TypedQuery<T> query = getQuery(spec, pageable);
		return pageable.isUnpaged() ? new PageImpl<>(query.getResultList())
				: readPageGrouped(query, getDomainClass(), pageable, spec);
	}

	private boolean hasNoGroupBy(Specification<T> spec) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(getDomainClass());
		Root<T> root = query.from(getDomainClass());

		if (spec != null) {
			Optional.ofNullable(spec.toPredicate(root, query, builder)).ifPresent(query::where);
		}

		return query.getGroupList().isEmpty();
	}

	protected <S extends T> Page<S> readPageGrouped(TypedQuery<S> query, final Class<S> domainClass, Pageable pageable,
			@Nullable Specification<S> spec) {
		if (pageable.isPaged()) {
			query.setFirstResult(PageableUtils.getOffsetAsInteger(pageable));
			query.setMaxResults(pageable.getPageSize());
		}

		return PageableExecutionUtils.getPage(query.getResultList(), pageable,
				() -> executeCountQueryGrouped(getCountQuery(spec, domainClass)));
	}

	private static long executeCountQueryGrouped(TypedQuery<Long> query) {
		Assert.notNull(query, "TypedQuery must not be null");
		return query.getResultList().size();
	}

}