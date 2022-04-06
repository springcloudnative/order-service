package com.polarbookshop.orderservice.application.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.polarbookshop.orderservice.domain.dto.Book;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Mono<Book> getBookByIsbn(String isbn) {
        return webClient
                .get()
                .uri(BOOKS_ROOT_API + isbn)
                .retrieve()
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
