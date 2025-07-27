package com.example.store.mapper;

import com.example.store.dto.response.ProductDTO;
import com.example.store.entity.OrderItem;
import com.example.store.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "orderItems", target = "orderIds", qualifiedByName = "orderItemsToOrderIds")
    ProductDTO productToProductDTO(Product product);

    List<ProductDTO> productsToProductDTOs(List<Product> products);

    @Named("orderItemsToOrderIds")
    default List<Long> orderItemsToOrderIds(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return Collections.emptyList(); // Return empty list instead of null
        }
        return orderItems.stream()
                .map(orderItem -> orderItem.getOrder().getId())
                .distinct() // Avoid duplicate order IDs if a product appears multiple times
                .toList();
    }
}
