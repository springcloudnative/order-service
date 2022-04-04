package com.polarbookshop.orderservice.infrastructure.repository;

import com.polarbookshop.orderservice.infrastructure.configuration.DataConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

@DataR2dbcTest
@Import(DataConfig.class)
@Testcontainers
public class OrderRepositoryR2dbcTests {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"));

    @Autowired
    OrderRepository orderRepository;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", OrderRepositoryR2dbcTests::r2dbcUrl);
        registry.add("spring.r2dbc.username", mysql::getUsername);
        registry.add("spring.r2dbc.password", mysql::getPassword);
        registry.add("spring.flyway.url", mysql::getJdbcUrl);
    }

    private static String r2dbcUrl() {
        return String.format("r2dbc:mysql://%s:%s/%s", mysql.getContainerIpAddress(),
                mysql.getMappedPort(MySQLContainer.MYSQL_PORT), mysql.getDatabaseName());
    }

    @Test
    void findOrderByIdWhenNotExisting() {
        StepVerifier.create(orderRepository.findById(394L))
                .expectNextCount(0)
                .verifyComplete();
    }
}
