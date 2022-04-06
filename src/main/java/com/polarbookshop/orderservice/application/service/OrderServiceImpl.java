package com.polarbookshop.orderservice.application.service;

import com.polarbookshop.orderservice.application.api.client.BookClient;
import com.polarbookshop.orderservice.domain.OrderStatus;
import com.polarbookshop.orderservice.domain.dto.Book;
import com.polarbookshop.orderservice.domain.repository.OrderRepository;
import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderServiceImpl implements OrderService {

    private final BookClient bookClient;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(BookClient bookClient, OrderRepository orderRepository) {
        this.bookClient = bookClient;
        this.orderRepository = orderRepository;
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
        return bookClient.getBookByIsbn(isbn)
                .map(book -> buildAcceptedOrder(book, quantity))
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save);
    }

    public static OrderEntity buildRejectedOrder(String bookIsbn, int quantity) {
        return OrderEntity.build(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
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
}
