package com.example.store.dto.response;

import java.util.List;

public record ProductDTO(Long id, String description, List<Long> orderIds) {}
