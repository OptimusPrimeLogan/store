package com.example.store.controller;

import com.example.store.dto.request.CreateProductRequest;
import com.example.store.dto.response.ProductDTO;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.mapper.ProductMapper;
import com.example.store.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@ComponentScan(basePackageClasses = ProductMapper.class)
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO(1L, "Ergonomic Wooden Keyboard", List.of(101L, 102L));
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Ergonomic Wooden Keyboard")))
                .andExpect(jsonPath("$[0].orderIds[0]", is(101)));
    }

    @Test
    void findProductById_shouldReturnProduct_whenFound() throws Exception {
        when(productService.findProductById(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(productDTO.description())));
    }

    @Test
    void findProductById_shouldReturn404_whenNotFound() throws Exception {
        when(productService.findProductById(anyLong())).thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(get("/products/999")).andExpect(status().isNotFound());
    }

    @Test
    void createProduct_shouldReturn201_whenRequestIsValid() throws Exception {
        CreateProductRequest request = new CreateProductRequest("A new amazing product");
        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(productDTO);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(productDTO.description())));
    }

    @Test
    void createProduct_shouldReturn400_whenRequestIsInvalid() throws Exception {
        CreateProductRequest invalidRequest = new CreateProductRequest("");

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
