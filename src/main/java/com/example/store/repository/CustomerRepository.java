package com.example.store.repository;

import com.example.store.entity.Customer;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Finds customers by performing a case-insensitive search for a substring within their name.
     *
     * @param name The substring to search for in the customer's name.
     * @return A list of matching customers.
     */
    @EntityGraph(attributePaths = {"orders", "orders.items", "orders.items.product"})
    List<Customer> findByNameContainingIgnoreCase(String name);

    @Override
    @EntityGraph(attributePaths = {"orders", "orders.items", "orders.items.product"})
    List<Customer> findAll();

    @Override
    @EntityGraph(attributePaths = {"orders", "orders.items", "orders.items.product"})
    Optional<Customer> findById(Long id);}
