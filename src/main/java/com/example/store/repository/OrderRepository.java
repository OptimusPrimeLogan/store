package com.example.store.repository;

import com.example.store.entity.Order;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders, eagerly loading their associated customers, items, and products.
     *
     * @return A list of all orders with their associated customers, items, and products.
     */
    @Override
    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    List<Order> findAll();

    /**
     * Finds an order by its ID, eagerly loading its associated customer, items, and products.
     *
     * @param id The ID of the order to find.
     * @return An Optional containing the order if found, or empty if not found.
     */
    @Override
    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    Optional<Order> findById(Long id);
}
