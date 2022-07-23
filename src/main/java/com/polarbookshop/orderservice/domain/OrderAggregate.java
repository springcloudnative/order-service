package com.polarbookshop.orderservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAggregate {

    private Long id;

    @NotBlank(message = "The book ISBN must be defined.")
    private String bookIsbn;

    private String bookName;

    private Double bookPrice;

    @NotNull(message = "The book quantity must be defined.")
    @Min(value = 1, message = "You must order at least 1 item.")
    @Min(value = 5, message = "You cannot order more than 5 items.")
    private Integer quantity;

    private OrderStatus status;

    private Instant createdDate;

    private Instant lastModifiedDate;

    private int version;
}
