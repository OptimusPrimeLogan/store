package com.example.store.controller;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.service.CustomerService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Gets a list of customers. Can be filtered by name using a query parameter.
     *
     * @param nameQuery Optional query string to filter customers by a substring of their name.
     * @return A list of customers.
     */
    @GetMapping
    public List<CustomerDTO> findCustomers(@RequestParam(name = "name", required = false) String nameQuery) {
        return customerService.findCustomers(nameQuery);
    }

    /**
     * Creates a new customer.
     *
     * @param customer The customer to create.
     * @return The created customer.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }
}
