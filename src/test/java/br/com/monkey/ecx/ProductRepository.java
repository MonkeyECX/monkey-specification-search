package br.com.monkey.ecx;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface ProductRepository
		extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

}
