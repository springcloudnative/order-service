package com.polarbookshop.orderservice.application.api.rest;

import com.polarbookshop.orderservice.application.service.OrderService;
import com.polarbookshop.orderservice.domain.OrderStatus;
import com.polarbookshop.orderservice.domain.dto.OrderRequest;
import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebFluxTest(OrderController.class)
@ActiveProfiles("integration")
class OrderControllerWebFluxTests {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private OrderService orderService;

    @Test
    void whenBookNotAvailableThenRejectOrder() {
        OrderRequest orderRequest = new OrderRequest("123456789", 3);
        OrderEntity expectedOrder = OrderService
                .buildRejectedOrder(orderRequest.getIsbn(), orderRequest.getQuantity());

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
                    assertThat(actualOrder.getStatus()).isEqualTo(OrderStatus.REJECTED);
        });
    }
}