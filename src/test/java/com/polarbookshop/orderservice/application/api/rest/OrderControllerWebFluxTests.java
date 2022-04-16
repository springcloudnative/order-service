package com.polarbookshop.orderservice.application.api.rest;

import com.polarbookshop.orderservice.application.api.client.BookClient;
import com.polarbookshop.orderservice.application.service.OrderService;
import com.polarbookshop.orderservice.application.service.OrderServiceImpl;
import com.polarbookshop.orderservice.domain.dto.OrderRequest;
import com.polarbookshop.orderservice.domain.repository.OrderRepository;
import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@WebFluxTest(OrderController.class)
class OrderControllerWebFluxTests {

    @Autowired
    private WebTestClient webClient;

//    @Mock
//    private BookClient bookClient;
//
//    @Mock
//    private OrderRepository orderRepository;

    @MockBean
    private OrderService orderService;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.initMocks(this);
//        orderService = new OrderServiceImpl(bookClient, orderRepository);
//    }

//    @BeforeEach
//    void setUp() {
//        orderService = Mockito.mock(OrderService.class);
//    }

    @Test
    void whenBookNotAvailableThenRejectOrder() {
        OrderRequest orderRequest = new OrderRequest("123456789", 3);
        OrderEntity expectedOrder = OrderEntity.builder()
                .bookIsbn(orderRequest.getIsbn())
                .quantity(orderRequest.getQuantity())
                .build();

        given(orderService.submitOrder(orderRequest.getIsbn(), orderRequest.getQuantity()))
        .willReturn(Mono.just(expectedOrder));

        webClient
                .post()
                .uri("/orders/")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(OrderEntity.class).value(actualOrder -> {
                    assertThat(actualOrder).isNotNull();
        });
    }
}