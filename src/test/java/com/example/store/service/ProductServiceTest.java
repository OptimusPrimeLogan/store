package com.example.store.service;

import com.example.store.dto.request.CreateProductRequest;
import com.example.store.dto.response.ProductDTO;
import com.example.store.entity.Product;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setDescription("Test Product");

        productDTO = new ProductDTO(1L, "Test Product", Collections.emptyList());
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.productsToProductDTOs(any())).thenReturn(List.of(productDTO));

        List<ProductDTO> result = productService.getAllProducts();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).description()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).productsToProductDTOs(any());
    }

    @Test
    void createProduct_shouldSaveAndReturnProductDTO() {
        CreateProductRequest request = new CreateProductRequest("New Product");
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        when(productRepository.save(productCaptor.capture())).thenReturn(product);
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        ProductDTO createdProduct = productService.createProduct(request);

        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.description()).isEqualTo("Test Product");

        Product capturedProduct = productCaptor.getValue();
        assertThat(capturedProduct.getDescription()).isEqualTo(request.description());

        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).productToProductDTO(any(Product.class));
    }

    @Test
    void findProductById_shouldReturnProduct_whenFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.productToProductDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.findProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        verify(productRepository, times(1)).findById(1L);
        verify(productMapper, times(1)).productToProductDTO(product);
    }

    @Test
    void findProductById_shouldThrowException_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 99");

        verify(productMapper, never()).productToProductDTO(any());
    }
}
