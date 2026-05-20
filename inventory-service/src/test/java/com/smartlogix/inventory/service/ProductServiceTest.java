package com.smartlogix.inventory.service;

import com.smartlogix.inventory.entity.Product;
import com.smartlogix.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setPymeId(100L);
        product.setName("Test Product");
        product.setTotalQuantity(50);
        product.setAvailableQuantity(50);
        product.setReservedQuantity(0);
    }

    @Test
    void testGetAllProductsByPyme() {
        // Given
        when(productRepository.findAllByPymeId(100L)).thenReturn(List.of(product));

        // When
        List<Product> products = productService.getAllProductsByPyme(100L);

        // Then
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Test Product", products.get(0).getName());
        verify(productRepository, times(1)).findAllByPymeId(100L);
    }

    @Test
    void testGetProductById() {
        // Given
        when(productRepository.findByIdAndPymeId(1L, 100L)).thenReturn(Optional.of(product));

        // When
        Optional<Product> result = productService.getProductById(1L, 100L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(productRepository, times(1)).findByIdAndPymeId(1L, 100L);
    }

    @Test
    void testCreateProduct() {
        // Given
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setTotalQuantity(20);

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        // When
        Product created = productService.createProduct(newProduct);

        // Then
        assertNotNull(created);
        assertEquals(2L, created.getId());
        assertEquals(20, created.getAvailableQuantity());
        assertEquals(0, created.getReservedQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        // Given
        Product productDetails = new Product();
        productDetails.setName("Updated Product");
        productDetails.setTotalQuantity(60); // Difference is +10

        when(productRepository.findByIdAndPymeId(1L, 100L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // When
        Optional<Product> updatedOpt = productService.updateProduct(1L, 100L, productDetails);

        // Then
        assertTrue(updatedOpt.isPresent());
        Product updated = updatedOpt.get();
        assertEquals("Updated Product", updated.getName());
        assertEquals(60, updated.getTotalQuantity());
        assertEquals(60, updated.getAvailableQuantity()); // 50 + 10
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testReserveStock_Success() {
        // Given
        when(productRepository.findByIdAndPymeId(1L, 100L)).thenReturn(Optional.of(product));

        // When
        boolean result = productService.reserveStock(1L, 100L, 10);

        // Then
        assertTrue(result);
        assertEquals(40, product.getAvailableQuantity());
        assertEquals(10, product.getReservedQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testReserveStock_NotEnoughStock() {
        // Given
        when(productRepository.findByIdAndPymeId(1L, 100L)).thenReturn(Optional.of(product));

        // When
        boolean result = productService.reserveStock(1L, 100L, 60);

        // Then
        assertFalse(result);
        assertEquals(50, product.getAvailableQuantity()); // Unchanged
        assertEquals(0, product.getReservedQuantity());   // Unchanged
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testConfirmReservation() {
        // Given
        product.setReservedQuantity(10);
        when(productRepository.findByIdAndPymeId(1L, 100L)).thenReturn(Optional.of(product));

        // When
        productService.confirmReservation(1L, 100L, 10);

        // Then
        assertEquals(0, product.getReservedQuantity());
        assertEquals(40, product.getTotalQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testCancelReservation() {
        // Given
        product.setAvailableQuantity(40);
        product.setReservedQuantity(10);
        when(productRepository.findByIdAndPymeId(1L, 100L)).thenReturn(Optional.of(product));

        // When
        productService.cancelReservation(1L, 100L, 10);

        // Then
        assertEquals(50, product.getAvailableQuantity());
        assertEquals(0, product.getReservedQuantity());
        verify(productRepository, times(1)).save(product);
    }
}