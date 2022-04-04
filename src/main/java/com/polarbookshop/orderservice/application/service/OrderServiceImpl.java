package com.polarbookshop.orderservice.application.service;

import com.polarbookshop.orderservice.domain.OrderStatus;
import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import com.polarbookshop.orderservice.infrastructure.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Flux<OrderEntity> getAllOrders() {
/*        List<OrderAggregate> orderList = orderRepository.findAll().toStream()
                .map(orderEntity -> OrderAggregate.builder()
                        .id(orderEntity.getId())
                        .bookIsbn(orderEntity.getBookIsbn())
                        .bookName(orderEntity.getBookName())
                        .bookPrice(orderEntity.getBookPrice())
                        .createdDate(orderEntity.getCreatedDate())
                        .lastModifiedDate(orderEntity.getLastModifiedDate())
                        .version(orderEntity.getVersion())
                        .build())
                .collect(Collectors.toList());

        return Flux.fromIterable(orderList);*/
        return orderRepository.findAll();
    }

    @Override
    public Mono<OrderEntity> submitOrder(String isbn, int quantity) {

/*        Mono<OrderEntity> orderMono = Mono.just(buildRejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save);

        return orderMono.flatMap(orderEntity -> {
            final OrderAggregate orderAggregate = orderEntity.toOrderAggregate();
            return Mono.just(orderAggregate);
        });*/

        return Mono.just(buildRejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save);
    }

    public static OrderEntity buildRejectedOrder(String bookIsbn, int quantity) {
        return OrderEntity.build(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
    }
}
