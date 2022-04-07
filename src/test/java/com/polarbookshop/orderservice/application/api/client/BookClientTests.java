package com.polarbookshop.orderservice.application.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.polarbookshop.orderservice.OrderServiceApplication;
import com.polarbookshop.orderservice.domain.dto.Book;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderServiceApplication.class)
@AutoConfigureStubRunner(
        ids = "com.polarbookshop.catalogservice:catalog-service:+:stubs",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
class BookClientTests {

    @Autowired
    private BookClient bookClient;

    @Test
    public void clientShouldReturnAnSpecificBook() {
        Mono<Book> book = bookClient.getBookByIsbn("1234561235");

        BDDAssertions.then(book).isNotNull();
    }
    
    @Test
    public void clientShouldReturnAllBooks() throws Exception {
        JsonNode result = this.bookClient.getAllBooks();

        assertTrue(result.isArray());

//        JsonNode firstBook = result.get(0);
//
//        System.out.println("BOOK: " + firstBook.toString());
    }
}