package com.polarbookshop.orderservice.infrastructure.repository;

import com.polarbookshop.orderservice.application.api.client.BookClient;
import com.polarbookshop.orderservice.application.service.OrderService;
import com.polarbookshop.orderservice.application.service.OrderServiceImpl;
import com.polarbookshop.orderservice.domain.OrderStatus;
import com.polarbookshop.orderservice.infrastructure.configuration.DataConfig;
import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    private OrderR2dbcRepository orderR2dbcRepository;

    @MockBean
    BookClient bookClient;

    private OrderMySqlDbRepository orderMySqlDbRepository;
    private OrderService orderService;

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

    @BeforeEach
    void setMockOutput() {
        orderMySqlDbRepository = new OrderMySqlDbRepository(orderR2dbcRepository);
        orderService = new OrderServiceImpl(bookClient, orderMySqlDbRepository);
    }

    @Test
    void findOrderByIdWhenNotExisting() {
        StepVerifier.create(orderMySqlDbRepository.findById(394L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void createRejectedOrder() {
        OrderEntity rejectedOrder = OrderServiceImpl.buildRejectedOrder("1234567890", 3);

        StepVerifier.create(orderMySqlDbRepository.save(rejectedOrder))
                .expectNextMatches(orderEntity -> orderEntity.getStatus().equals(OrderStatus.REJECTED))
                .verifyComplete();
    }
}
