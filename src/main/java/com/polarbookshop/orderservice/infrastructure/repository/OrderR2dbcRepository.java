package com.polarbookshop.orderservice.infrastructure.repository;

import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderR2dbcRepository extends ReactiveCrudRepository<OrderEntity, Long> {
}
