package com.example.store.service;

import com.example.store.dto.request.CreateProductRequest;
import com.example.store.dto.response.ProductDTO;
import com.example.store.entity.Product;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.ProductRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductDTO> getAllProducts() {
        return productMapper.productsToProductDTOs(productRepository.findAll());
    }

    @Transactional
    public ProductDTO createProduct(@Valid CreateProductRequest productRequest) {
        Product newProduct = new Product();
        newProduct.setDescription(productRequest.description());
        return productMapper.productToProductDTO(productRepository.save(newProduct));
    }

    public ProductDTO findProductById(Long id) {
        return productMapper.productToProductDTO(productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id)));
    }
}
