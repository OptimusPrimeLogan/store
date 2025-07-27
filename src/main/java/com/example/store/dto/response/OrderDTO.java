package com.example.store.dto.response;

import java.util.List;

public record OrderDTO(
        Long id,
        String description,
        OrderCustomerDTO customer,
        List<ProductInOrderDTO> products) {}
