package br.com.monkey.sdk;

import br.com.monkey.sdk.configuration.MonkeyJpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Sql(statements = "SET SESSION sql_mode=''")
@Import({ MonkeyJpaRepositoriesAutoConfiguration.class })
public class MysqlIntegrationContainerConfiguration {

	@Container
	public static final MySQLContainer<?> database = new MySQLContainer<>("mysql:8.0.32").withUsername("root")
		.withReuse(true);

	@DynamicPropertySource
	static void databaseProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", database::getJdbcUrl);
		registry.add("spring.datasource.username", database::getUsername);
		registry.add("spring.datasource.password", database::getPassword);
	}

}
