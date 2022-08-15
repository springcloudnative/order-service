package com.polarbookshop.orderservice.infrastructure.repository;

import com.polarbookshop.orderservice.domain.repository.OrderRepository;
import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class OrderMySqlDbRepository implements OrderRepository {

    private final OrderR2dbcRepository orderRepository;

    @Autowired
    public OrderMySqlDbRepository(OrderR2dbcRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Flux<OrderEntity> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Mono<OrderEntity> submitOrder(OrderEntity orderEntity) {
        return this.save(orderEntity);
    }

    @Override
    public Mono<OrderEntity> save(OrderEntity orderEntity) {
        return orderRepository.save(orderEntity);
    }

    @Override
    public Mono<OrderEntity> findById(Long id) {
        return orderRepository.findById(id);
    }
}
