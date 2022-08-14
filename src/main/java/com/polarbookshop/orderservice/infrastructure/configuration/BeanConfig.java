package com.polarbookshop.orderservice.infrastructure.configuration;

import com.polarbookshop.orderservice.application.api.client.BookClient;
import com.polarbookshop.orderservice.application.service.OrderService;
import com.polarbookshop.orderservice.application.service.OrderServiceImpl;
import com.polarbookshop.orderservice.domain.repository.OrderRepository;
import org.apache.kafka.clients.producer.internals.TransactionManager;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    OrderService orderService(BookClient bookClient, OrderRepository orderRepository, StreamBridge streamBridge) {
        return new OrderServiceImpl(bookClient, orderRepository, streamBridge);
    }

}
