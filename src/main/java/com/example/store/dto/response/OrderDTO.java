package com.example.store.dto.response;

import java.util.List;
import java.util.UUID;

public record OrderDTO(Long id, UUID orderNumber, OrderCustomerDTO customer, List<ProductInOrderDTO> products) {}
