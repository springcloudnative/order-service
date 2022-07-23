package com.polarbookshop.orderservice.application.api.rest;

import com.polarbookshop.orderservice.domain.dto.OrderRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class OrderRequestJsonTests {

    @Autowired
    private JacksonTester<OrderRequest> json;

    @Test
    void testDeserialize() throws Exception {
        JSONObject jsonObject = new JSONObject();
        String content = jsonObject
                .put("isbn", "1234567890")
                .put("quantity", 1)
                .toString();

        assertThat(this.json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(new OrderRequest("1234567890", 1));
    }
}
