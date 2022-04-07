package com.polarbookshop.orderservice.application.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.polarbookshop.orderservice.domain.dto.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * This class is used to send HTTP calls to the
 * CatalogService's GET endpoints exposed through
 * its API.
 * The WebClient will ultimately return a Book object
 * wrapped in a Mono publisher.
 */
@Component
public class BookClient {

    private static final String BOOKS_ROOT_API = "/books/";
    private final WebClient webClient;

    @Autowired
    public BookClient(WebClient webClient) {

        this.webClient = webClient;
    }

    /**
     * When the timeout expires, instead of throwing an exception, a
     * fallback behavior is provided.
     * If the Order Service cannot accept an order if the book availability
     * is not verified, an empty result is returned so that the order will be
     * rejected.
     * An exponential backoff is used as the retry strategy. 3 attempts allowed
     * and 100ms initial backoff.
     * The onErrorResume() operator is added to define a fallback when a 404 response
     * is received.
     * It is added after the timeout() operator and before the retryWhen() so if 404
     * is received, the retry operator is not triggered.
     * @param isbn
     * @return
     */
    public Mono<Book> getBookByIsbn(String isbn) {
/*        return webClient
                .get()
                .uri(BOOKS_ROOT_API + isbn)
                .retrieve()
                .bodyToMono(Book.class)
                .timeout(Duration.ofSeconds(2), Mono.empty())
                .onErrorResume(
                        WebClientResponseException.NotFound.class, exception -> Mono.empty()
                )
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)));*/

        return webClient
                .get()
                .uri(BOOKS_ROOT_API + isbn)
                .retrieve()
                .onStatus(HttpStatus::isError,
                        response -> Mono.error(new RuntimeException("ClientError")))
                .bodyToMono(Book.class);
    }

    public JsonNode getAllBooks() {
        JsonNode availableBooks = webClient.get()
                .uri(BOOKS_ROOT_API)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return availableBooks;
    }
}
