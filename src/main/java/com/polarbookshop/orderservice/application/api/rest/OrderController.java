package com.polarbookshop.orderservice.application.api.rest;

import com.polarbookshop.orderservice.application.service.OrderService;
import com.polarbookshop.orderservice.domain.dto.OrderRequest;
import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Flux<OrderEntity> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping
    public Mono<OrderEntity> submitOrder(@RequestBody @Valid OrderRequest orderRequest) {
        return orderService.submitOrder(orderRequest.getIsbn(), orderRequest.getQuantity());
    }
}
