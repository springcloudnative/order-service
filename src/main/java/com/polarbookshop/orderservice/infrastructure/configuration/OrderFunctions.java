package com.polarbookshop.orderservice.infrastructure.configuration;

import com.polarbookshop.orderservice.application.service.OrderService;
import com.polarbookshop.orderservice.domain.OrderStatus;
import com.polarbookshop.orderservice.domain.dto.OrderDispatchedMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class OrderFunctions {

    @Bean
    public Consumer<OrderDispatchedMessageDTO> dispatchOrder(OrderService orderService) {
        return orderDispatchedMessage -> {
            log.info("The order with id {} has been dispatched", orderDispatchedMessage.getOrderId());
            orderService.updateOrderStatus(orderDispatchedMessage.getOrderId(), OrderStatus.DISPATCHED);
        };
    }
}
