package br.com.monkey.ecx;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface CategoryRepository
		extends JpaRepository<Category, Integer>, JpaSpecificationExecutor<Category> {

}
