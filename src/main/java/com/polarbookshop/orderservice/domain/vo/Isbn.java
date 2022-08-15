package com.polarbookshop.orderservice.domain.vo;

import lombok.Value;
import org.springframework.util.ObjectUtils;

import java.beans.Transient;
import java.io.Serializable;
import java.util.function.Predicate;

@Value
public class Isbn implements Serializable {
    private static final long serialVersionUID = -7408978229480038597L;

    private String value;

    private Isbn() {
        this.value = null;
    }

    public Isbn(String value) {
        this.value = value;
    }

    public Isbn change(String value) {
        return new Isbn(value);
    }

    @Transient
    public boolean isValidIsbn() {
        Predicate<Isbn> validIsbnPredicate = isbn ->
                !ObjectUtils.isEmpty(isbn.getValue()) &&
                        isbn.getValue().matches("^([0-9]{10}|[0-9]{13})$");

        return validIsbnPredicate.test(this);
    }
}
