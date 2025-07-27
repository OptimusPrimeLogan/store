package com.example.store.service;

import com.example.store.dto.request.CreateOrderRequest;
import com.example.store.dto.response.OrderDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    private Order order;
    private OrderDTO orderDTO;
    private Customer customer;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        product1 = new Product();
        product1.setId(101L);
        product1.setDescription("Product One");

        product2 = new Product();
        product2.setId(102L);
        product2.setDescription("Product Two");

        order = new Order();
        order.setId(1L);
        order.setOrderNumber(UUID.randomUUID());
        order.setCustomer(customer);

        orderDTO = new OrderDTO(order.getId(), order.getOrderNumber(), null, List.of());
    }

    @Test
    void getAllOrders_shouldReturnListOfOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.ordersToOrderDTOs(any())).thenReturn(List.of(orderDTO));

        List<OrderDTO> result = orderService.getAllOrders();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(order.getId());
        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).ordersToOrderDTOs(any());
    }

    @Test
    void findOrderById_shouldReturnOrder_whenFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.findOrderById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void findOrderById_shouldThrowException_whenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findOrderById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found with id: 99");

        verify(orderMapper, never()).orderToOrderDTO(any());
    }

    @Test
    void createOrder_shouldSucceed_withValidRequest() {
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(101L, 102L));
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(request.productIds())).thenReturn(List.of(product1, product2));
        when(orderRepository.save(orderCaptor.capture())).thenReturn(order);
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.createOrder(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(order.getId());

        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(capturedOrder.getItems()).hasSize(2);
        assertThat(capturedOrder.getItems().get(0).getProduct().getId()).isEqualTo(product1.getId());

        verify(customerRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findAllById(request.productIds());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_shouldThrowException_whenCustomerNotFound() {
        CreateOrderRequest request = new CreateOrderRequest(99L, List.of(101L));
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with id: 99");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_shouldThrowException_whenProductNotFound() {
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(101L, 999L));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(request.productIds())).thenReturn(List.of(product1));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("One or more products not found.");

        verify(orderRepository, never()).save(any());
    }
}
