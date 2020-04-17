package br.com.monkey.ecx;

import br.com.monkey.ecx.specification.SpecificationsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringSpecificationSearchApplication.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SpecificationBuilderTest {

	@Autowired
	private ProductRepository productRepository;

	private Product keyboard;

	private Product mouse;

	private Product camera;

	private Product monitor;

	@BeforeEach
	public void before() {
		Category electronics = Category.builder().id(1).name("electronics")
				.updatedAt(Instant.ofEpochMilli(1586980512000L)).build();

		keyboard = Product.builder().id(1).name("Keyboard").price(99.99F).stock(10)
				.createdAt(Instant.ofEpochMilli(1586980512000L)).category(electronics)
				.build();

		mouse = Product.builder().id(2).name("Mouse").price(200.19F).stock(1)
				.createdAt(Instant.now()).category(electronics).build();

		monitor = Product.builder().id(3).name("Monitor").price(1233.19F).stock(1)
				.createdAt(Instant.now()).category(electronics).build();

		camera = Product.builder().id(4).name("Camera Conitere").price(2341.22F)
				.stock(200).createdAt(Instant.now()).category(Category.builder().id(2)
						.name("Surveillance").updatedAt(Instant.now()).build())
				.build();

		productRepository.saveAll(asList(keyboard, mouse, monitor, camera));
	}

	@Test
	public void should_return_product_when_find_by_name_equality() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("name:Keyboard").build();

		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_starts_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("name:Mo*").build();

		assertEquals(asList(mouse, monitor), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_ends_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("name:*tor").build();

		assertEquals(singletonList(monitor), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_contains_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("name:*nit*").build();

		assertEquals(asList(monitor, camera), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_not_starts_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("name!Mo*").build();

		assertEquals(asList(keyboard, camera), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_not_ends_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("name!*tor").build();

		assertEquals(asList(keyboard, mouse, camera),
				productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_not_contains_with() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("name!*nit*").build();

		assertEquals(asList(keyboard, mouse), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_price_greater_then() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("price>200").build();

		assertEquals(asList(mouse, monitor, camera),
				productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_price_less_then() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("price<100").build();

		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_and_price() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("price<100 AND name:Keyboard").build();

		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_or_price() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("price<100 OR name:Mouse").build();

		assertEquals(asList(keyboard, mouse), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_name_or_price_and_name() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("name:Keyboard OR (name:Mouse AND price>199)").build();

		assertEquals(asList(keyboard, mouse), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_in_depth_by_updated_at_less_then() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("updatedAt<2020-04-16").build();
		assertEquals(asList(keyboard, mouse, monitor),
				productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_created_at_equality() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("createdAt:2020-04-15").build();
		assertEquals(singletonList(keyboard), productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_created_at_greater_then() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("createdAt>2020-04-16").build();
		assertEquals(asList(mouse, monitor, camera),
				productRepository.findAll(specification));
	}

	@Test
	public void should_return_product_when_find_by_created_at_not_equal() {
		Specification<Product> specification = new SpecificationsBuilder<Product>()
				.withSearch("createdAt!2020-04-15").build();
		assertEquals(asList(mouse, monitor, camera),
				productRepository.findAll(specification));
	}

}
