package com.polarbookshop.orderservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    private String isbn;
    private String title;
    private String author;
    private Double price;
    private String publisher;

    public Book(String bookIsbn, String title, String author, double v) {
    }
}
