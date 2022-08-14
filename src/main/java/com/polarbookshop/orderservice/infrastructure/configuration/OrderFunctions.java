package com.polarbookshop.orderservice.infrastructure.configuration;

import com.polarbookshop.orderservice.application.service.OrderService;
import com.polarbookshop.orderservice.domain.OrderStatus;
import com.polarbookshop.orderservice.domain.events.OrderDispatchedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class OrderFunctions {

    /**
     * For each dispatched message, it updates
     * the order status in the database
     * @param orderService
     * @return
     */
    @Bean
    public Consumer<Message<OrderDispatchedEvent>> dispatchOrder(OrderService orderService) {
        return orderDispatchedMessage -> {
            log.info("The order with id {} has been dispatched",
                    orderDispatchedMessage.getPayload().getOrderId());

            orderService.updateOrderStatus(orderDispatchedMessage.getPayload().getOrderId(),
                    OrderStatus.DISPATCHED);
        };
    }
}
