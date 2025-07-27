package com.example.store.dto.response;

import java.util.Set;

public record CustomerDTO (Long id, String name, Set<CustomerOrderDTO> orders) {}
