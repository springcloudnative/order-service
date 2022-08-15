package com.polarbookshop.orderservice.application.service;

import com.polarbookshop.orderservice.application.api.client.BookClient;
import com.polarbookshop.orderservice.domain.OrderStatus;
import com.polarbookshop.orderservice.domain.dto.Book;
import com.polarbookshop.orderservice.domain.events.OrderAcceptedEvent;
import com.polarbookshop.orderservice.domain.repository.OrderRepository;
import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final BookClient bookClient;
    private final OrderRepository orderRepository;
    private final StreamBridge streamBridge;

    @Value("${spring.cloud.stream.bindings.acceptOrder-out-0.destination}")
    private String topic;

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
     * Define a saga transaction with database update and
     * message production.
     * To ensure the atomicity of this process, both operations
     * are wrapped in a local transaction.
     *
     * - Calls the Book Service to check the book disponibility.
     * - If the book is available, it accepts the order.
     * - If not available, it rejects the order.
     * - Saves the order (either accepted or rejected).
     * @param isbn
     * @param quantity
     * @return
     */
    @Override
    @Transactional
    public Mono<OrderEntity> submitOrder(String isbn, int quantity) {
        return bookClient.getBookByIsbn(isbn)
                .map(book -> buildAcceptedOrder(book, quantity))
                .defaultIfEmpty(OrderService.buildRejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save)
                .doOnNext(this::publishOrderAcceptedEvent);
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

    /**
     * Method used for publishing events to a
     * given destination.
     * It accepts and OrderEntity object as input,
     * and sends it to a Kafka topic destination using
     * StreamBridge.
     * @param order
     */
    @Override
    public void publishOrderAcceptedEvent(OrderEntity order) {
        // if the order is not accepted, it does nothing
        if (!order.getStatus().equals(OrderStatus.ACCEPTED)) {
            return;
        }

        // building the event to notify and order has been accepted
        OrderAcceptedEvent orderAcceptedEvent = new OrderAcceptedEvent(order.getId());
        log.info("Sending order accepted event with id: {}", order.getId());

        // explicitly sends an event to the "acceptOrder-out-0" binding
        boolean result = streamBridge.send(this.topic, orderAcceptedEvent);
        log.info("Result of sending data for order with id {}: {}", order.getId(), result);
    }
}
