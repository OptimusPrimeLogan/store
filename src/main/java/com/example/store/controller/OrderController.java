package com.example.store.controller;

import com.example.store.dto.request.CreateOrderRequest;
import com.example.store.dto.response.OrderDTO;
import com.example.store.service.OrderService;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Timed(value = "order.find.time", description = "Measures the time taken to retrieve orders")
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Timed(value = "order.create.time", description = "Measures the time taken to create an order")
    public OrderDTO createOrder(@Valid @RequestBody CreateOrderRequest orderRequest){
        return orderService.createOrder(orderRequest);
    }

    @GetMapping("/{id}")
    @Timed(value = "order.find.by.id.time", description = "Measures the time taken to retrieve an order by ID")
    public OrderDTO getOrderById(@PathVariable Long id){
        return orderService.findOrderById(id);
    }
}
