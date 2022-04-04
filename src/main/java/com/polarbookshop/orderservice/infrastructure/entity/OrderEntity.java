package com.polarbookshop.orderservice.infrastructure.entity;

import com.polarbookshop.orderservice.domain.OrderAggregate;
import com.polarbookshop.orderservice.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("orders")
public class OrderEntity {

    @Id
    Long id;

    String bookIsbn;
    String bookName;
    Double bookPrice;
    Integer quantity;
    OrderStatus status;

    @CreatedDate
    Instant createdDate;

    @LastModifiedDate
    Instant lastModifiedDate;

    @Version
    int version;

    public OrderAggregate toOrderAggregate() {
        return OrderAggregate.builder()
                .id(this.getId())
                .bookIsbn(this.getBookIsbn())
                .bookName(this.getBookName())
                .bookPrice(this.getBookPrice())
                .quantity(this.getQuantity())
                .status(this.getStatus())
                .createdDate(this.getCreatedDate())
                .lastModifiedDate(this.lastModifiedDate)
                .version(this.getVersion())
                .build();
    }

    public static OrderEntity build(String bookIsbn, String bookName,
                                    Double bookPrice, Integer quantity,
                                    OrderStatus status) {

        return OrderEntity.builder()
                .id(null)
                .bookIsbn(bookIsbn)
                .bookName(bookName)
                .bookPrice(bookPrice)
                .quantity(quantity)
                .status(status)
                .createdDate(null)
                .lastModifiedDate(null)
                .version(0)
                .build();
    }

    public static OrderEntity createFromAggregate(OrderAggregate orderAggregate) {
        return OrderEntity.builder()
                .bookIsbn(orderAggregate.getBookIsbn())
                .quantity(orderAggregate.getQuantity())
                .build();
    }
}
