package br.com.monkey.ecx;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface SupplierRepository
		extends JpaRepository<Supplier, Integer>, JpaSpecificationExecutor<Supplier> {

}
