package com.example.store.service;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public List<CustomerDTO> findCustomers(String nameQuery) {
        if (StringUtils.hasText(nameQuery)) {
            return customerMapper.customersToCustomerDTOs(customerRepository.findByNameContainingIgnoreCase(nameQuery));
        } else {
            return customerMapper.customersToCustomerDTOs(customerRepository.findAll());
        }
    }

    @Transactional
    public CustomerDTO createCustomer(Customer customer) {
        var savedCustomer = customerRepository.save(customer);
        return customerMapper.customerToCustomerDTO(savedCustomer);
    }
}
