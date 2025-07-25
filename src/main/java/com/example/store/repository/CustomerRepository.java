package com.example.store.repository;

import com.example.store.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Finds customers by performing a case-insensitive search for a substring within their name.
     *
     * @param name The substring to search for in the customer's name.
     * @return A list of matching customers.
     */
    List<Customer> findByNameContainingIgnoreCase(String name);
}
