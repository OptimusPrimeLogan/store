package com.example.store.service;

import com.example.store.dto.request.CreateOrderRequest;
import com.example.store.dto.response.OrderDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.OrderItem;
import com.example.store.entity.Product;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;

import com.example.store.repository.ProductRepository;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    /**
     * Retrieves all orders from the repository and maps them to OrderDTOs.
     *
     * @return A list of OrderDTOs representing all orders.
     */
    public List<OrderDTO> getAllOrders() {
        return orderMapper.ordersToOrderDTOs(orderRepository.findAll());
    }

    /**
     * Creates a new order based on the provided CreateOrderRequest.
     * The order is associated with a customer and a list of products.
     *
     * @param request The request containing order details, customer ID, and product IDs.
     * @return The created OrderDTO.
     * @throws ResourceNotFoundException if the customer or any of the products do not exist.
     */
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        // 1. Find the customer
        Customer customer = customerRepository
                .findById(request.customerId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Customer not found with id: " + request.customerId()));

        // 2. Create the base order
        Order newOrder = new Order();
        newOrder.setDescription(request.description());
        newOrder.setCustomer(customer);

        // 3. Find all products and create OrderItems
        List<Product> products = productRepository.findAllById(request.productIds());
        if (products.size() != request.productIds().size()) {
            throw new ResourceNotFoundException("One or more products not found.");
        }

        for (Product product : products) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            newOrder.addItem(orderItem);
        }

        return orderMapper.orderToOrderDTO(orderRepository.save(newOrder));
    }

    /**
     * Finds an order by its ID and returns it as an OrderDTO.
     *
     * @param id The ID of the order to find.
     * @return The OrderDTO corresponding to the found order.
     * @throws ResourceNotFoundException if no order is found with the given ID.
     */
    public OrderDTO findOrderById(Long id) {
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.orderToOrderDTO(order);
    }
}
