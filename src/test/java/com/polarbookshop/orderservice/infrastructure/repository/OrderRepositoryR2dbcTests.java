package com.polarbookshop.orderservice.infrastructure.repository;

import com.polarbookshop.orderservice.application.api.client.BookClient;
import com.polarbookshop.orderservice.application.service.OrderService;
import com.polarbookshop.orderservice.application.service.OrderServiceImpl;
import com.polarbookshop.orderservice.domain.OrderStatus;
import com.polarbookshop.orderservice.infrastructure.configuration.DataConfig;
import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

@DataR2dbcTest
@Import(DataConfig.class)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@Slf4j
@ActiveProfiles("integration")
public class OrderRepositoryR2dbcTests {

    @Autowired
    private OrderR2dbcRepository orderR2dbcRepository;

    @MockBean
    BookClient bookClient;

    private OrderMySqlDbRepository orderMySqlDbRepository;
    private OrderService orderService;

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
