package com.example.store.controller;

import com.example.store.dto.request.CreateCustomerRequest;
import com.example.store.dto.response.CustomerDTO;
import com.example.store.mapper.CustomerMapper;
import com.example.store.service.CustomerService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@ComponentScan(basePackageClasses = CustomerMapper.class)
class CustomerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customerDTO = new CustomerDTO(1L, "John Doe", Collections.emptySet());
    }

    @Test
    void createCustomer_shouldReturn201_whenRequestIsValid() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest("John Doe");
        when(customerService.createCustomer(any(CreateCustomerRequest.class))).thenReturn(customerDTO);

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(customerDTO.id()))
                .andExpect(jsonPath("$.name").value(customerDTO.name()));
    }

    @Test
    void createCustomer_shouldReturn400_whenNameIsBlank() throws Exception {
        CreateCustomerRequest invalidRequest = new CreateCustomerRequest("");

        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findCustomers_shouldReturnAllCustomers_whenNoQueryIsProvided() throws Exception {
        when(customerService.findCustomers(null)).thenReturn(List.of(customerDTO));

        mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(customerDTO.id()))
                .andExpect(jsonPath("$[0].name").value(customerDTO.name()));
    }

    @Test
    void findCustomers_shouldReturnFilteredCustomers_whenQueryIsProvided() throws Exception {
        String query = "John";
        when(customerService.findCustomers(query)).thenReturn(List.of(customerDTO));

        mockMvc.perform(get("/customer").param("name", query))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value(customerDTO.name()));
    }

    @Test
    void findCustomers_shouldReturnEmptyList_whenNoCustomersMatchQuery() throws Exception {
        when(customerService.findCustomers(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customer").param("name", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }
}
