package br.com.monkey.sdk;

import br.com.monkey.sdk.core.exception.BadRequestException;
import br.com.monkey.sdk.specification.SpecificationsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@DirtiesContext
@AutoConfigureTestDatabase(replace = NONE)
@ContextConfiguration(classes = { SpringSpecificationSearchApplication.class })
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=update", "spring.flyway.enabled=false",
		"monkey.flyway.enabled=false", "internationalization.country=BR", "internationalization.language=pt-BR",
		"internationalization.timezone=GMT-3" })
class SpecificationBuilderTest extends MysqlIntegrationContainerConfiguration {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	private Category electronics;

	private Category others;

	private Product keyboard;

	private Product mouse;

	private Product camera;

	private Product monitor;

	@BeforeEach
	public void before() {
		electronics = Category.builder()
			.id(1)
			.name("electronics")
			.updatedAt(Instant.ofEpochMilli(1586980512000L))
			.type(CategoryType.TYPE)
			.build();

		others = Category.builder()
			.id(1)
			.name("others")
			.updatedAt(Instant.ofEpochMilli(1586980512000L))
			.type(CategoryType.TYPE2)
			.build();

		keyboard = Product.builder()
			.id(1)
			.name("Keyboard")
			.price(99.99F)
			.stock(10)
			.createdAt(Instant.ofEpochMilli(1586980512000L))
			.visible(true)
			.category(electronics)
			.suppliers(asList(
					Supplier.builder()
						.id(1)
						.name("SupplierKeyboards1")
						.practicedPrice(BigDecimal.valueOf(150))
						.governmentId("32752846000174")
						.updatedAt(Instant.ofEpochMilli(1586980512000L))
						.build(),
					Supplier.builder()
						.id(2)
						.name("SupplierKeyboards2")
						.practicedPrice(BigDecimal.valueOf(145))
						.governmentId("01368989000153")
						.updatedAt(Instant.ofEpochMilli(1586980512000L))
						.build()))
			.build();

		mouse = Product.builder()
			.id(2)
			.name("Mouse")
			.price(200.19F)
			.stock(1)
			.visible(true)
			.createdAt(Instant.now())
			.category(electronics)
			.build();

		monitor = Product.builder()
			.id(3)
			.name("Monitor")
			.price(1233.19F)
			.stock(1)
			.createdAt(Instant.now())
			.visible(true)
			.category(electronics)
			.suppliers(asList(
					Supplier.builder()
						.id(3)
						.name("SupplierMonitors1")
						.practicedPrice(BigDecimal.valueOf(800))
						.governmentId("20544215000180")
						.updatedAt(Instant.ofEpochMilli(1586980512000L))
						.build(),
					Supplier.builder()
						.id(4)
						.name("SupplierMonitors2")
						.practicedPrice(BigDecimal.valueOf(650))
						.governmentId("67119333000180")
						.updatedAt(Instant.ofEpochMilli(1586980512000L))
						.build()))
			.build();

		camera = Product.builder()
			.id(4)
			.name("Camera Conitere")
			.price(2341.22F)
			.stock(200)
			.createdAt(Instant.now())
			.visible(false)
			.category(Category.builder().id(2).name("Surveillance").updatedAt(Instant.now()).build())
			.build();

		productRepository.saveAll(asList(keyboard, mouse, monitor, camera));
	}

	@Test
	public void should_return_products_when_find_by_array_in_depth_by_government_id_and_return_keyboard() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.governmentId IN ['32752846000174']")
			.build();
		assertEquals(List.of(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_products_when_find_by_array_in_depth_by_government_id_and_return_mouse() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.governmentId IN ['20544215000180']")
			.build();
		assertEquals(List.of(monitor), productRepository.findAll(specification));
	}

	@Test
	public void should_return_products_when_find_by_array_in_depth_by_government_id() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.governmentId IN ['20544215000180', '32752846000174']")
			.build();
		assertEquals(List.of(keyboard, monitor), productRepository.findAll(specification));
	}

	@Test
	public void should_return_products_when_find_by_array_in_depth() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.id IN [1,2,3,4]")
			.build();
		assertEquals(List.of(keyboard, monitor), productRepository.findAll(specification));
	}

	@Test
	public void should_return_products_when_find_by_array() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("id IN [1,2]").build();
		assertEquals(List.of(keyboard, mouse), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_equality() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("name:Keyboard").build();

		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_starts_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("name:Mo*").build();

		assertEquals(asList(mouse, monitor), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_ends_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("name:*tor").build();

		assertEquals(singletonList(monitor), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_contains_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("name:*nit*").build();

		assertEquals(asList(monitor, camera), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_not_starts_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("name!Mo*").build();

		assertEquals(asList(keyboard, camera), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_not_ends_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("name!*tor").build();

		assertEquals(asList(keyboard, mouse, camera), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_not_contains_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("name!*nit*").build();

		assertEquals(asList(keyboard, mouse), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_price_greater_then() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("price>200").build();

		assertEquals(asList(mouse, monitor, camera), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_price_less_then() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("price<100").build();

		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_and_price() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("price<100 AND name:Keyboard")
			.build();

		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_or_price() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("price<100 OR name:Mouse")
			.build();

		assertEquals(asList(keyboard, mouse), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_or_price_and_name() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("name:Keyboard OR (name:Mouse AND price>199)")
			.build();

		assertEquals(asList(keyboard, mouse), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_in_depth_by_updated_at_less_then() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("updatedAt<2020-04-16")
			.build();
		assertEquals(asList(keyboard, mouse, monitor), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_created_at_equality() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("createdAt:2020-04-15")
			.build();
		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_created_at_greater_then() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("createdAt>2020-04-16")
			.build();
		assertEquals(asList(mouse, monitor, camera), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_created_at_not_equal() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("createdAt!2020-04-15")
			.build();
		assertEquals(asList(mouse, monitor, camera), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_visible_equal_true() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("visible:true").build();
		assertEquals(asList(keyboard, mouse, monitor), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_visible_equal_false() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("visible:false").build();
		assertEquals(singletonList(camera), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_visible_not_equal_true() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("visible!true").build();
		assertEquals(singletonList(camera), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_visible_not_equal_false() {
		Specification<Product> specification = new SpecificationsBuilder<Product>().withSearch("visible!false").build();
		assertEquals(asList(keyboard, mouse, monitor), productRepository.findAll(specification));
	}

	@Test
	public void should_find_by_enum() {
		Specification<Category> specification = new SpecificationsBuilder<Category>().withSearch("type:TYPE").build();
		assertEquals(singletonList(electronics), categoryRepository.findAll(specification));
	}

	@Test
	public void should_find_by_enum_with_unrecognized_value() {
		Specification<Category> specification = new SpecificationsBuilder<Category>().withSearch("type:FOO").build();
		assertThrows(BadRequestException.class, () -> categoryRepository.findAll(specification));
	}

	@Test
	public void should_find_by_enum_with_invalid_operation() {
		Specification<Category> specification = new SpecificationsBuilder<Category>().withSearch("type>FOO").build();
		assertThrows(BadRequestException.class, () -> categoryRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_supplier_government_id_equality() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.governmentId:32752846000174")
			.build();
		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_supplier_government_not_equality() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.governmentId!32752846000174")
			.build();
		assertNotEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_supplier_practiced_price_greater_then() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.practicedPrice>500")
			.build();
		assertEquals(singletonList(monitor), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_supplier_practiced_price_less_then() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.practicedPrice<600")
			.build();
		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_supplier_by_name_starts_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.name:SupplierKey*")
			.build();
		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_supplier_by_government_id_ends_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.governmentId:*180")
			.build();
		assertEquals(singletonList(monitor), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_supplier_by_name_contains_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.name:*Keyboards*")
			.build();
		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_supplier_by_name_not_starts_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.name!SupplierKeybo*")
			.build();
		assertEquals(singletonList(monitor), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_supplier_by_government_id_not_ends_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.governmentId!*180")
			.build();
		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_supplier_by_name_not_contains_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
			.withSearch("suppliers.name!*Monitors*")
			.build();
		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

}
