package com.polarbookshop.orderservice;

import com.polarbookshop.orderservice.application.api.client.BookClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Slf4j
class OrderServiceApplicationTestsIT {
	@Container
	static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private BookClient bookClient;

	@DynamicPropertySource
	static void mysqlProperties(DynamicPropertyRegistry registry) {
		String jdbcUrl = mySQLContainer.getJdbcUrl();
		registry.add("spring.r2dbc.url", OrderServiceApplicationTestsIT::r2dbcUrl);
		registry.add("spring.r2dbc.username", mySQLContainer::getUsername);
		registry.add("spring.r2dbc.password", mySQLContainer::getPassword);
		registry.add("spring.flyway.url", mySQLContainer::getJdbcUrl);
	}


	private static String r2dbcUrl() {
		return String.format("r2dbc:mysql://%s:%s%s",
				mySQLContainer.getContainerIpAddress(),
				mySQLContainer.getMappedPort(MySQLContainer.MYSQL_PORT),
				mySQLContainer.getDatabaseName());
	}

	@Test
	void contextLoads() {
	}

}
