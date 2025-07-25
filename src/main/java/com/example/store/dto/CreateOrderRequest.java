package com.example.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "Order description cannot be empty")
    private String description;

    @NotNull(message = "An order must be associated with a customer") private Long customerId;
}
