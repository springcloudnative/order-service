package com.polarbookshop.orderservice.infrastructure.converters;

import com.polarbookshop.orderservice.domain.vo.Isbn;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IsbnReadingConverter implements Converter<String, Isbn> {

    @Override
    public Isbn convert(String isbn) {
        return new Isbn(isbn);
    }
}
