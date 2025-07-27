package com.example.store.integration;

import com.example.store.dto.request.CreateOrderRequest;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        Customer savedCustomer = customerRepository.save(customer);

        Product product1 = new Product();
        product1.setDescription("Laptop");
        Product savedProduct1 = productRepository.save(product1);

        Product product2 = new Product();
        product2.setDescription("Mouse");
        Product savedProduct2 = productRepository.save(product2);

        CreateOrderRequest request =
                new CreateOrderRequest(savedCustomer.getId(), List.of(savedProduct1.getId(), savedProduct2.getId()));

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.customer.name", is("Test Customer")))
                .andExpect(jsonPath("$.products", hasSize(2)))
                .andExpect(jsonPath("$.products[?(@.description == 'Laptop')]").exists())
                .andExpect(jsonPath("$.products[?(@.description == 'Mouse')]").exists());
    }

    @Test
    void shouldFindOrderById() throws Exception {
        Customer customer = new Customer();
        customer.setName("Order Find Customer");
        Customer savedCustomer = customerRepository.save(customer);

        Order order = new Order();
        order.setCustomer(savedCustomer);
        order.setOrderNumber(UUID.randomUUID());
        Order savedOrder = orderRepository.save(order);

        mockMvc.perform(get("/order/{id}", savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedOrder.getId().intValue())))
                .andExpect(
                        jsonPath("$.orderNumber", is(savedOrder.getOrderNumber().toString())))
                .andExpect(jsonPath("$.customer.name", is("Order Find Customer")));
    }

    @Test
    void shouldReturn404WhenCreatingOrderWithNonExistentCustomer() throws Exception {
        Product product1 = new Product();
        product1.setDescription("Laptop");
        Product savedProduct1 = productRepository.save(product1);

        CreateOrderRequest request = new CreateOrderRequest(9999L, List.of(savedProduct1.getId()));
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenCreatingOrderWithNonExistentProduct() throws Exception {
        Customer customer = new Customer();
        customer.setName("Order Find Customer");
        Customer savedCustomer = customerRepository.save(customer);

        CreateOrderRequest request = new CreateOrderRequest(savedCustomer.getId(), List.of(1L));
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
