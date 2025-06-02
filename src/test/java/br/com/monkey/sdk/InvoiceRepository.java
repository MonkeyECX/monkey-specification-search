package br.com.monkey.sdk;

import br.com.monkey.sdk.jpa.MonkeyJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface InvoiceRepository extends MonkeyJpaRepository<Invoice, Integer>, JpaSpecificationExecutor<Invoice> {

}