package com.polarbookshop.orderservice.domain.repository;

import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository {

    Flux<OrderEntity> findAll();
    Mono<OrderEntity> submitOrder(OrderEntity orderEntity);
    Mono<OrderEntity> save(OrderEntity orderEntity);
    Mono<OrderEntity> findById(Long id);
}
