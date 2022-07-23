package com.polarbookshop.orderservice.infrastructure.converters;

import com.polarbookshop.orderservice.domain.vo.Isbn;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IsbnWritingConverter implements Converter<Isbn, String> {

    @Override
    public String convert(Isbn isbn) {
        return isbn == null ? null : isbn.getValue();
    }
}
