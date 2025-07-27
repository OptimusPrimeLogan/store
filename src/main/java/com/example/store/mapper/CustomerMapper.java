package com.example.store.mapper;

import com.example.store.dto.response.CustomerDTO;
import com.example.store.entity.Customer;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = OrderMapper.class)
public interface CustomerMapper {
    CustomerDTO customerToCustomerDTO(Customer customer);

    List<CustomerDTO> customersToCustomerDTOs(List<Customer> customer);
}
