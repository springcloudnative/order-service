package com.polarbookshop.orderservice.application.api.rest;

import com.polarbookshop.orderservice.domain.OrderStatus;
import com.polarbookshop.orderservice.infrastructure.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class OrderJsonTests {

    @Autowired
    private JacksonTester<OrderEntity> json;

    @Test
    void testSerialize() throws Exception {
        OrderEntity order = OrderEntity.builder()
                    .id(394L)
                    .bookIsbn("1234567890")
                    .bookName("Book Name")
                    .bookPrice(9.90)
                    .quantity(1)
                    .status(OrderStatus.REJECTED)
                    .createdDate(Instant.now())
                    .lastModifiedDate(Instant.now())
                    .version(21)
                    .build();

        JsonContent<OrderEntity> jsonContent = json.write(order);

        assertThat(jsonContent).extractingJsonPathNumberValue("@.id")
                .isEqualTo(order.getId().intValue());

        assertThat(jsonContent).extractingJsonPathStringValue("@.bookIsbn")
                .isEqualTo(order.getBookIsbn());

        assertThat(jsonContent).extractingJsonPathStringValue("@.bookName")
                .isEqualTo(order.getBookName());

        assertThat(jsonContent).extractingJsonPathNumberValue("@.bookPrice")
                .isEqualTo(order.getBookPrice());

        assertThat(jsonContent).extractingJsonPathNumberValue("@.quantity")
                .isEqualTo(order.getQuantity());

        assertThat(jsonContent).extractingJsonPathStringValue("@.status")
                .isEqualTo(order.getStatus().toString());

        assertThat(jsonContent).extractingJsonPathStringValue("@.createdDate")
                .isEqualTo(order.getCreatedDate().toString());

        assertThat(jsonContent).extractingJsonPathStringValue("@.lastModifiedDate")
                .isEqualTo(order.getLastModifiedDate().toString());

        assertThat(jsonContent).extractingJsonPathNumberValue("@.version")
                .isEqualTo(order.getVersion());
    }

}