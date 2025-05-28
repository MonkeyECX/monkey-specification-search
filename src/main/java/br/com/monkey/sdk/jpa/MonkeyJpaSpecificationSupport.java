package br.com.monkey.sdk.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

interface MonkeyJpaSpecificationSupport<T> {

	Page<T> findAllGroupBy(Specification<T> spec, Pageable pageable);

}