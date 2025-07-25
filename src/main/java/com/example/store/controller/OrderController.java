package com.example.store.controller;

import com.example.store.dto.CreateOrderRequest;
import com.example.store.dto.OrderDTO;
import com.example.store.service.OrderService;

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
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@Valid @RequestBody CreateOrderRequest orderRequest){
        return orderService.createOrder(orderRequest);
    }

    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable Long id){
        return orderService.findOrderById(id);
    }
}
