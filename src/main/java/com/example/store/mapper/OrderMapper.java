package com.example.store.mapper;

import com.example.store.dto.response.CustomerOrderDTO;
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
    OrderDTO orderToOrderDTO(Order order);

    List<OrderDTO> ordersToOrderDTOs(List<Order> orders);

    // This new method tells MapStruct how to map an Order to a CustomerOrderDTO.
    // It will be automatically used by CustomerMapper.
    CustomerOrderDTO orderToCustomerOrderDTO(Order order);

    // This method is used to map the nested customer object within an OrderDTO
    OrderCustomerDTO customerToOrderCustomerDTO(Customer customer);

    // This single method correctly delegates the mapping of an OrderItem
    // to a ProductInOrderDTO to the ProductMapper. This is the best practice
    // for reusability and keeping logic centralized.
    @Mapping(source = "product", target = ".")
    ProductInOrderDTO orderItemToProductInOrderDTO(OrderItem item);
}
