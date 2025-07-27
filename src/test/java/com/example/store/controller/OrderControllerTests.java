package com.example.store.controller;

import com.example.store.dto.request.CreateOrderRequest;
import com.example.store.dto.response.OrderCustomerDTO;
import com.example.store.dto.response.OrderDTO;
import com.example.store.dto.response.ProductInOrderDTO;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.mapper.OrderMapper;
import com.example.store.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ComponentScan(basePackageClasses = OrderMapper.class)
class OrderControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private OrderDTO orderDTO;
    private OrderCustomerDTO orderCustomerDTO;
    private ProductInOrderDTO productInOrderDTO;

    @BeforeEach
    void setUp() {
        UUID orderNumber = UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef");

        orderCustomerDTO = new OrderCustomerDTO(1L, "John Doe");
        productInOrderDTO = new ProductInOrderDTO(101L, "Sample Product");
        orderDTO = new OrderDTO(1L, orderNumber, orderCustomerDTO, List.of(productInOrderDTO));
    }

    @Test
    void createOrder_shouldReturn201_whenRequestIsValid() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(101L));
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(orderDTO);

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderDTO.id()))
                .andExpect(
                        jsonPath("$.orderNumber").value(orderDTO.orderNumber().toString()))
                .andExpect(jsonPath("$.customer.id").value(orderCustomerDTO.id()))
                .andExpect(jsonPath("$.customer.name").value(orderCustomerDTO.name()))
                .andExpect(jsonPath("$.products[0].id").value(productInOrderDTO.id()))
                .andExpect(jsonPath("$.products[0].description").value(productInOrderDTO.description()));
    }

    @Test
    void createOrder_shouldReturn400_whenRequestIsInvalid() throws Exception {
        CreateOrderRequest invalidRequest = new CreateOrderRequest(null, Collections.emptyList());

        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllOrders_shouldReturnListOfOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(orderDTO));

        mockMvc.perform(get("/order"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(orderDTO.id()))
                .andExpect(jsonPath("$[0].orderNumber")
                        .value(orderDTO.orderNumber().toString()));
    }

    @Test
    void getOrderById_shouldReturnOrder_whenFound() throws Exception {
        when(orderService.findOrderById(1L)).thenReturn(orderDTO);

        mockMvc.perform(get("/order/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(orderDTO.id()))
                .andExpect(jsonPath("$.customer.id").value(orderCustomerDTO.id()));
    }

    @Test
    void getOrderById_shouldReturn404_whenNotFound() throws Exception {
        when(orderService.findOrderById(anyLong())).thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/order/999")).andExpect(status().isNotFound());
    }
}
