package com.example.store.controller;

import com.example.store.dto.request.CreateCustomerRequest;
import com.example.store.dto.response.CustomerDTO;
import com.example.store.service.CustomerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.micrometer.core.annotation.Timed;

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
    @Timed(value = "customer.find.time", description = "Measures the time taken to retrieve customers")
    public List<CustomerDTO> findCustomers(@RequestParam(name = "name", required = false) String nameQuery) {
        return customerService.findCustomers(nameQuery);
    }

    /**
     * Creates a new customer.
     *
     * @param customerRequest The customer to create.
     * @return The created customer.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Timed(value = "customer.create.time", description = "Measures the time taken to create a customer")
    public CustomerDTO createCustomer(@Valid @RequestBody CreateCustomerRequest customerRequest) {
        return customerService.createCustomer(customerRequest);
    }
}
