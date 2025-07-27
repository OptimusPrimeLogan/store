package com.example.store.service;

import com.example.store.dto.response.CustomerDTO;
import com.example.store.dto.request.CreateCustomerRequest;
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

    /**
     * Retrieves a list of customers based on the provided name query.
     * If the name query is empty or null, all customers are returned.
     *
     * @param nameQuery The substring to search for in customer names.
     * @return A list of CustomerDTOs matching the search criteria.
     */
    public List<CustomerDTO> findCustomers(String nameQuery) {
        if (StringUtils.hasText(nameQuery)) {
            return customerMapper.customersToCustomerDTOs(customerRepository.findByNameContainingIgnoreCase(nameQuery));
        } else {
            return customerMapper.customersToCustomerDTOs(customerRepository.findAll());
        }
    }

    /**
     * Creates a new customer and saves it to the repository.
     *
     * @param newCustomerRequest The customer entity to be created.
     * @return The created CustomerDTO.
     */
    @Transactional
    public CustomerDTO createCustomer(CreateCustomerRequest newCustomerRequest) {
        Customer newCustomer = new Customer();
        newCustomer.setName(newCustomerRequest.name());
        var savedCustomer = customerRepository.save(newCustomer);
        return customerMapper.customerToCustomerDTO(savedCustomer);
    }
}
