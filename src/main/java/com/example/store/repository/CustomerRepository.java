package com.example.store.repository;

import com.example.store.entity.Customer;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Finds a customer by their ID, eagerly loading their orders and the products within those orders.
     *
     * @param name The part of name of the customer to find.
     * @return An Optional containing the customer if found, or empty if not found.
     */
    @EntityGraph(attributePaths = {"orders", "orders.items", "orders.items.product"})
    List<Customer> findByNameContainingIgnoreCase(String name);

    /**
     * Finds all customers, eagerly loading their orders and the products within those orders.
     *
     * @return A list of all customers with their associated orders and products.
     */
    @Override
    @EntityGraph(attributePaths = {"orders", "orders.items", "orders.items.product"})
    List<Customer> findAll();
}
