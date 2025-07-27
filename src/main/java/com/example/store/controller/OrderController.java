package com.example.store.controller;

import com.example.store.dto.request.CreateOrderRequest;
import com.example.store.dto.response.OrderDTO;
import com.example.store.service.OrderService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.micrometer.core.annotation.Timed;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Retrieves all orders.
     *
     * @return A list of all orders.
     */
    @GetMapping
    @Timed(value = "order.find.time", description = "Measures the time taken to retrieve orders")
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * Creates a new order.
     *
     * @param orderRequest The order to create.
     * @return The created order.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Timed(value = "order.create.time", description = "Measures the time taken to create an order")
    public OrderDTO createOrder(@Valid @RequestBody CreateOrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id The ID of the order to retrieve.
     * @return The order with the specified ID.
     */
    @GetMapping("/{id}")
    @Timed(value = "order.find.by.id.time", description = "Measures the time taken to retrieve an order by ID")
    public OrderDTO getOrderById(@PathVariable Long id) {
        return orderService.findOrderById(id);
    }
}
