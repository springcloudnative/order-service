package com.polarbookshop.orderservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotBlank(message = "The book ISBN must be defined.")
    public String isbn;

    @NotNull(message = "The book quantity must be defined.")
    @Min(value = 1, message = "You must order at least 1 item.")
    @Max(value = 5, message = "You cannot order more than 5 items.")
    public Integer quantity;
}
