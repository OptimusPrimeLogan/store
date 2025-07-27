package com.example.store.integration;

import com.example.store.dto.request.CreateProductRequest;
import com.example.store.entity.Product;
import com.example.store.repository.ProductRepository;

import jakarta.persistence.EntityManagerFactory;

import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private Statistics statistics;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        statistics =
                entityManagerFactory.unwrap(org.hibernate.SessionFactory.class).getStatistics();
        statistics.clear();
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        Product product = new Product();
        product.setDescription("Test Keyboard");
        productRepository.save(product);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is("Test Keyboard")));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        CreateProductRequest request = new CreateProductRequest("A new amazing product");

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("A new amazing product")));
    }

    @Test
    void shouldFindProductById() throws Exception {
        Product product = new Product();
        product.setDescription("Find Me Product");
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(get("/products/{id}", savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(savedProduct.getId().intValue())))
                .andExpect(jsonPath("$.description", is("Find Me Product")));
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        mockMvc.perform(get("/products/9999")).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForInvalidCreateProductRequest() throws Exception {
        CreateProductRequest invalidRequest = new CreateProductRequest("");

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCacheProductOnSecondFindById() throws Exception {
        Product product = new Product();
        product.setDescription("A cachable product");
        Product savedProduct = productRepository.save(product);

        // First Call (should be a cache miss)
        mockMvc.perform(get("/products/{id}", savedProduct.getId())).andExpect(status().isOk());

        assertThat(statistics.getSecondLevelCacheMissCount()).isEqualTo(1);
        assertThat(statistics.getSecondLevelCachePutCount()).isEqualTo(1);
        assertThat(statistics.getSecondLevelCacheHitCount()).isZero();

        // Second Call (should be a cache hit)
        mockMvc.perform(get("/products/{id}", savedProduct.getId())).andExpect(status().isOk());

        // Verify the cache was hit and no new misses/puts occurred
        assertThat(statistics.getSecondLevelCacheMissCount()).isEqualTo(1);
        assertThat(statistics.getSecondLevelCachePutCount()).isEqualTo(1);
        assertThat(statistics.getSecondLevelCacheHitCount()).isEqualTo(1);
    }
}
