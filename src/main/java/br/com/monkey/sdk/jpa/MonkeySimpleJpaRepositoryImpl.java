package br.com.monkey.sdk.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.val;
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

public class MonkeySimpleJpaRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements MonkeyJpaSpecificationSupport<T> {

	public MonkeySimpleJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
	}

	@Override
	public Page<T> findAllUsingGroupBy(Specification<T> spec, Pageable pageable) {
		TypedQuery<T> query = getQuery(spec, pageable);
		return pageable.isUnpaged() ? new PageImpl<>(query.getResultList())
				: readPageGrouped(query, getDomainClass(), pageable, spec);
	}

	protected <S extends T> Page<S> readPageGrouped(TypedQuery<S> query, final Class<S> domainClass, Pageable pageable,
			@Nullable Specification<S> spec) {
		if (pageable.isPaged()) {
			query.setFirstResult(PageableUtils.getOffsetAsInteger(pageable));
			query.setMaxResults(pageable.getPageSize());
		}

		val result = query.getResultList();
		return PageableExecutionUtils.getPage(result, pageable,
				() -> executeCountQueryGrouped(getCountQuery(spec, domainClass)));
	}

	private static long executeCountQueryGrouped(TypedQuery<Long> query) {
		Assert.notNull(query, "TypedQuery must not be null");
		return query.getResultList().size();
	}

}