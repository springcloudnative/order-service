package com.polarbookshop.orderservice.application.service;

import com.polarbookshop.orderservice.application.api.client.BookClient;
import com.polarbookshop.orderservice.domain.OrderStatus;
import com.polarbookshop.orderservice.domain.dto.Book;
import com.polarbookshop.orderservice.domain.dto.OrderAcceptedMessageDTO;
import com.polarbookshop.orderservice.domain.dto.OrderRequest;
import com.polarbookshop.orderservice.domain.repository.OrderRepository;
import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final BookClient bookClient;
    private final OrderRepository orderRepository;
    private final StreamBridge streamBridge;

    @Autowired
    public OrderServiceImpl(BookClient bookClient, OrderRepository orderRepository, StreamBridge streamBridge) {
        this.bookClient = bookClient;
        this.orderRepository = orderRepository;
        this.streamBridge = streamBridge;
    }

    @Override
    public Flux<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * - Calls the Book Service to check the book disponibility.
     * - If the book is available, it accepts the order.
     * - If not available, it rejects the order.
     * - Saves the order (either accepted or rejected).
     * @param isbn
     * @param quantity
     * @return
     */
    @Override
    public Mono<OrderEntity> submitOrder(String isbn, int quantity) {
        Mono<OrderEntity> orderEntity = bookClient.getBookByIsbn(isbn)
                .map(book -> buildAcceptedOrder(book, quantity))
                .defaultIfEmpty(OrderService.buildRejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save)
                .doOnNext(this::publishOrderAcceptedEvent);

        return orderEntity;
    }

    /**
     * When an order is accepted, we must specify ISBN, book name (title + author),
     * quantity and status.
     * SpringData takes care of adding identifier, version, and audit metadata.
     * @param book
     * @param quantity
     * @return
     */
    public static OrderEntity buildAcceptedOrder(Book book, int quantity) {
        return OrderEntity.build(book.getIsbn(), book.getTitle().concat("-").concat(book.getAuthor()),
                book.getPrice(), quantity, OrderStatus.ACCEPTED);
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        orderRepository.findById(orderId)
                .map(existingOrder ->
                        new OrderEntity(
                                existingOrder.getId(),
                                existingOrder.getBookIsbn(),
                                existingOrder.getBookName(),
                                existingOrder.getBookPrice(),
                                existingOrder.getQuantity(),
                                status,
                                existingOrder.getCreatedDate(),
                                existingOrder.getLastModifiedDate(),
                                existingOrder.getVersion()
                        ))
                .flatMap(orderRepository::save)
                .subscribe();
    }

    @Override
    public void publishOrderAcceptedEvent(OrderEntity order) {

        if (!order.getStatus().equals(OrderStatus.ACCEPTED)) {
            return;
        }

        OrderAcceptedMessageDTO orderAcceptedMessage = new OrderAcceptedMessageDTO(order.getId());
        log.info("Sending order accepted event with id: {}", order.getId());
        var result = streamBridge.send("order-accepted", orderAcceptedMessage);
        log.info("Result of sending data for order with id {}: {}", order.getId(), result);
    }
}
