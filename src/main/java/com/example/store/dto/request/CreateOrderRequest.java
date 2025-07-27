package com.example.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank(message = "Order description cannot be empty") String description,
        @NotNull(message = "An order must be associated with a customer") Long customerId,
        @NotEmpty(message = "An order must contain at least one product") List<Long> productIds) {}
