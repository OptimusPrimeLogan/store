package com.example.store.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest (@NotBlank(message = "Customer name cannot be empty") String name) {
}
