package com.example.store.repository;

import com.example.store.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Finds all products and eagerly fetches their associated order items and orders.*
     * @return a list of all products with their associated order items and orders
     */
    @Override
    @EntityGraph(attributePaths = { "orderItems", "orderItems.order" })
    List<Product> findAll();
}