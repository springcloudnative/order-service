package com.polarbookshop.orderservice.infrastructure.repository;

import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderRepository extends ReactiveCrudRepository<OrderEntity, Long> {
}
