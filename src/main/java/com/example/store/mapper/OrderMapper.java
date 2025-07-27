package com.example.store.mapper;

import com.example.store.dto.response.OrderCustomerDTO;
import com.example.store.dto.response.OrderDTO;
import com.example.store.dto.response.ProductInOrderDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;

import com.example.store.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface OrderMapper {
    @Mapping(source = "items", target = "products")
    @Mapping(source = "customer", target = "customer")
    OrderDTO orderToOrderDTO(Order order);

    List<OrderDTO> ordersToOrderDTOs(List<Order> orders);

    OrderCustomerDTO orderToOrderCustomerDTO(Customer customer);

    //    - It takes an OrderItem as input.
    //    - The @Mapping(source = "product", target = ".") instruction means:
    //      "for the output, use the 'product' field from the source OrderItem
    //      and map it using a suitable mapper".
    //    - Since we added 'uses = ProductMapper.class', MapStruct finds the
    //      productToProductInOrderDTO method in ProductMapper and uses it.
    @Mapping(source = "product", target = ".")
    ProductInOrderDTO mapOrderItemToProductInOrderDTO(OrderItem item);
}
