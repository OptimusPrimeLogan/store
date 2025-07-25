package com.example.store.service;

import com.example.store.dto.CreateOrderRequest;
import com.example.store.dto.OrderDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;

    public List<OrderDTO> getAllOrders() {
        return orderMapper.ordersToOrderDTOs(orderRepository.findAll());
    }

    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository
                .findById(request.getCustomerId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        Order newOrder = new Order();
        newOrder.setDescription(request.getDescription());
        newOrder.setCustomer(customer);

        return orderMapper.orderToOrderDTO(orderRepository.save(newOrder));
    }

    public OrderDTO findOrderById(Long id) {
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.orderToOrderDTO(order);
    }
}
