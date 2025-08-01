package br.com.monkey.sdk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@DirtiesContext
@AutoConfigureTestDatabase(replace = NONE)
@ContextConfiguration(classes = { SpringSpecificationSearchApplication.class })
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=update", "spring.flyway.enabled=false",
		"monkey.flyway.enabled=false", "internationalization.country=BR", "internationalization.language=pt-BR",
		"internationalization.timezone=GMT-3" })
class MonkeyJpaRepositoryTest extends MysqlIntegrationContainerConfiguration {

	@Autowired
	private InvoiceRepository repository;

	private Pageable pageable;

	@BeforeEach
	public void before() {
		pageable = PageRequest.of(0, 1);

		List<Invoice> invoices = new java.util.ArrayList<>();
		invoices.add(Invoice.builder().id(1).invoiceKey("001").installment(1).governmentId("governmentId").build());
		invoices.add(Invoice.builder().id(2).invoiceKey("001").installment(2).governmentId("governmentId").build());

		invoices.add(Invoice.builder().id(3).invoiceKey("002").installment(1).governmentId("governmentId").build());
		invoices.add(Invoice.builder().id(4).invoiceKey("002").installment(2).governmentId("governmentId").build());
		invoices.add(Invoice.builder().id(5).invoiceKey("002").installment(3).governmentId("governmentId").build());

		invoices.add(Invoice.builder().id(6).invoiceKey("003").installment(1).governmentId("governmentId").build());
		invoices.add(Invoice.builder().id(7).invoiceKey("003").installment(2).governmentId("governmentId").build());
		invoices.add(Invoice.builder().id(8).invoiceKey("003").installment(3).governmentId("governmentId").build());

		invoices.add(Invoice.builder().id(9).invoiceKey("004").installment(1).governmentId("governmentId").build());
		invoices.add(Invoice.builder().id(10).invoiceKey("004").installment(2).governmentId("governmentId").build());
		repository.saveAll(invoices);
	}

	@Test
	public void return_zero_elements_when_invoice_key_not_found() {
		int expectedTotalRows = 0;
		int expectedRowsGrouped = 0;

		Specification<Invoice> specGrouped = (root, query, criteriaBuilder) -> {
			query.groupBy(root.get("invoiceKey"));
			return criteriaBuilder.equal(root.get("invoiceKey"), "not-found");
		};

		Specification<Invoice> specNotGrouped = (root, query, criteriaBuilder) -> criteriaBuilder
			.equal(root.get("invoiceKey"), "not-found");

		assertEquals(expectedRowsGrouped, repository.findAll(specGrouped, pageable).getTotalElements());
		assertEquals(expectedTotalRows, repository.findAll(specNotGrouped, pageable).getTotalElements());
	}

	@Test
	public void return_elements_when_search_one_invoice_key() {
		int expectedTotalRows = 3;
		int expectedRowsGrouped = 1;

		Specification<Invoice> specGrouped = (root, query, criteriaBuilder) -> {
			query.groupBy(root.get("invoiceKey"));
			return criteriaBuilder.equal(root.get("invoiceKey"), "003");
		};

		Specification<Invoice> specNotGrouped = (root, query, criteriaBuilder) -> criteriaBuilder
			.equal(root.get("invoiceKey"), "003");

		assertEquals(expectedRowsGrouped, repository.findAll(specGrouped, pageable).getTotalElements());
		assertEquals(expectedTotalRows, repository.findAll(specNotGrouped, pageable).getTotalElements());
	}

	@Test
	public void return_elements_when_search_two_invoice_key() {
		int expectedTotalRows = 6;
		int expectedRowsGrouped = 2;

		Specification<Invoice> specGrouped = (root, query, criteriaBuilder) -> {
			query.groupBy(root.get("invoiceKey"));
			return root.get("invoiceKey").in("002", "003");
		};

		Specification<Invoice> specNotGrouped = (root, query, criteriaBuilder) -> root.get("invoiceKey")
			.in("002", "003");

		assertEquals(expectedRowsGrouped, repository.findAll(specGrouped, pageable).getTotalElements());
		assertEquals(expectedTotalRows, repository.findAll(specNotGrouped, pageable).getTotalElements());
	}

	@Test
	public void return_elements_when_search_one_government_id() {
		int expectedTotalRows = 10;
		int expectedRowsGrouped = 4;

		Specification<Invoice> specGrouped = (root, query, criteriaBuilder) -> {
			query.groupBy(root.get("invoiceKey"));
			return criteriaBuilder.equal(root.get("governmentId"), "governmentId");
		};

		Specification<Invoice> specNotGrouped = (root, query, criteriaBuilder) -> criteriaBuilder
			.equal(root.get("governmentId"), "governmentId");

		assertEquals(expectedRowsGrouped, repository.findAll(specGrouped, pageable).getTotalElements());
		assertEquals(expectedTotalRows, repository.findAll(specNotGrouped, pageable).getTotalElements());
	}

}