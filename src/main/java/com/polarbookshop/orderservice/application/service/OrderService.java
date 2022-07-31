package com.polarbookshop.orderservice.application.service;

import com.polarbookshop.orderservice.domain.OrderStatus;
import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {
    Flux<OrderEntity> getAllOrders();
    Mono<OrderEntity> submitOrder(String isbn, int quantity);
    void updateOrderStatus(Long orderId, OrderStatus status);
    static OrderEntity buildRejectedOrder(String bookIsbn, int quantity) {
        return OrderEntity.build(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
    }
    void publishOrderAcceptedEvent(OrderEntity order);
}
