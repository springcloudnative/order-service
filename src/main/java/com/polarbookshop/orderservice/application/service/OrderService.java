package com.polarbookshop.orderservice.application.service;

import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {
    Flux<OrderEntity> getAllOrders();
    Mono<OrderEntity> submitOrder(String isbn, int quantity);
}
