package com.polarbookshop.orderservice.application.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.polarbookshop.orderservice.domain.dto.Book;
import com.polarbookshop.orderservice.infrastructure.configuration.ClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

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
    private final ClientProperties clientProperties;

    @Autowired
    public BookClient(WebClient webClient, ClientProperties clientProperties) {

        this.webClient = webClient;
        this.clientProperties = clientProperties;
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
        return webClient
                .get()
                .uri(BOOKS_ROOT_API + isbn)
                .retrieve()
                .bodyToMono(Book.class)
                .timeout(Duration.ofSeconds(clientProperties.clientTimeOut), Mono.empty())
                .onErrorResume(
                        WebClientResponseException.NotFound.class, exception -> Mono.empty()
                )
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)));
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
