package com.polarbookshop.orderservice.application.api.rest;

import com.polarbookshop.orderservice.domain.dto.OrderRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class OrderRequestValidationTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrectThenValidationSucceeds() {
        OrderRequest orderRequest = new OrderRequest("1234567890", 1);
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(orderRequest);

        assertThat(violations).isEmpty();
    }

    @Test
    void whenIsbnNotDefinedThenValidationFails() {
        OrderRequest orderRequest = new OrderRequest("", 1);
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(orderRequest);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book ISBN must be defined.");
    }

    @ParameterizedTest
    @MethodSource("isbnAnQuantityProvider")
    void orderQuantityValues_tests(String isbn, Integer quantity, String errorMessage) {
        OrderRequest orderRequest = new OrderRequest(isbn, quantity);
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(orderRequest);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(errorMessage);
    }

    static Stream<Arguments> isbnAnQuantityProvider() {
        return Stream.of(
                arguments("1234567890", null, "The book quantity must be defined."),
                arguments("1234567890", 0, "You must order at least 1 item."),
                arguments("1234567890", 7, "You cannot order more than 5 items.")
        );
    }
}
