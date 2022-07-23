package com.polarbookshop.orderservice.application.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.polarbookshop.orderservice.domain.dto.Book;
import com.polarbookshop.orderservice.infrastructure.configuration.ClientProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = ClientProperties.class)
@TestPropertySource("classpath:application.yml")
public class BookClientTests {

    private MockWebServer mockWebServer;
    private BookClient bookClient;

    @Autowired
    private ClientProperties clientProperties;

    @BeforeEach
    public void setup() throws IOException {
        this.mockWebServer = new MockWebServer();

        // starts the mock server before running a test case
        this.mockWebServer.start();

        // uses the mock server URL as the base URL for WebClient
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").uri().toString())
                .build();

        this.bookClient = new BookClient(webClient, this.clientProperties);
        System.out.println("ClientProperties: " + this.clientProperties);
    }

    @AfterEach
    public void clean() throws IOException {
        // shuts the mock server down after completing a test case
        this.mockWebServer.shutdown();
    }

    @Test
    public void whenBookExistsThenReturnBook() throws JsonProcessingException, InterruptedException {
        String bookIsbn = "1234567890";

        JSONObject jsonObject = new JSONObject();
        String content = jsonObject
                .put("isbn", bookIsbn)
                .put("title", "Title")
                .put("author", "Author")
                .put("price", 9.90)
                .put("publisher", "Polarsophia").toString();

        // defines the response to be returned by the mock server
        MockResponse mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(content);

        // adds a mock response to the queue processed by the mock server
        mockWebServer.enqueue(mockResponse);

        Mono<Book> book = bookClient.getBookByIsbn(bookIsbn);

        StepVerifier.create(book)
                .expectNextMatches(b -> b.getIsbn().equals(bookIsbn))
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/books/1234567890", request.getPath());
    }

    @Test
    public void whenBookNotExistsThenReturnEmpty() {
        String bookIsbn = "1234567891";

        MockResponse mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(404);

        mockWebServer.enqueue(mockResponse);

        StepVerifier
                .create(bookClient.getBookByIsbn(bookIsbn))
                .expectNextCount(0)
                .verifyComplete();
    }
}