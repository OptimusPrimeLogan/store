package com.example.store.controller;

import com.example.store.dto.request.CreateProductRequest;
import com.example.store.dto.response.ProductDTO;
import com.example.store.service.ProductService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.micrometer.core.annotation.Timed;

import java.util.List;

/** Controller for managing products in the store. Provides endpoints to retrieve and create products. */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Retrieves all products.
     *
     * @return a list of ProductDTO representing all products.
     */
    @GetMapping
    @Timed(value = "product.find.time", description = "Measures the time taken to retrieve products")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * Creates a new product.
     *
     * @param productRequest the request containing product details.
     * @return the created ProductDTO.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Timed(value = "product.create.time", description = "Measures the time taken to create a product")
    public ProductDTO createProduct(@Valid @RequestBody CreateProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product to retrieve.
     * @return the ProductDTO representing the product with the specified ID.
     */
    @GetMapping("/{id}")
    @Timed(value = "product.find.by.id.time", description = "Measures the time taken to retrieve a product by ID")
    public ProductDTO findProductById(@PathVariable Long id) {
        return productService.findProductById(id);
    }
}
