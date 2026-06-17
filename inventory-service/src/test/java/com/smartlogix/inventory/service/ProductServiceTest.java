package com.smartlogix.inventory.service;

import com.smartlogix.inventory.dto.ProductRequest;
import com.smartlogix.inventory.dto.ProductResponse;
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
        when(productRepository.findAllByPymeId(100L)).thenReturn(List.of(product));

        List<ProductResponse> products = productService.getAllProductsByPyme(100L);

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("Test Product", products.get(0).getName());
        verify(productRepository, times(1)).findAllByPymeId(100L);
    }

    @Test
    void testGetProductById() {
        when(productRepository.findByIdAndPymeId(1L, 100L)).thenReturn(Optional.of(product));

        Optional<ProductResponse> result = productService.getProductById(1L, 100L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(productRepository, times(1)).findByIdAndPymeId(1L, 100L);
    }

    @Test
    void testCreateProduct() {
        ProductRequest request = new ProductRequest();
        request.setName("New Product");
        request.setTotalQuantity(20);

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        ProductResponse created = productService.createProduct(request, 100L);

        assertNotNull(created);
        assertEquals(2L, created.getId());
        assertEquals(20, created.getAvailableQuantity());
        assertEquals(0, created.getReservedQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");
        request.setTotalQuantity(60);

        when(productRepository.findByIdAndPymeId(1L, 100L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Optional<ProductResponse> updatedOpt = productService.updateProduct(1L, 100L, request);

        assertTrue(updatedOpt.isPresent());
        assertEquals("Updated Product", updatedOpt.get().getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testReserveStock_Success() {
        when(productRepository.reserveStockAtomic(1L, 100L, 10)).thenReturn(1);

        boolean result = productService.reserveStock(1L, 100L, 10);

        assertTrue(result);
        verify(productRepository, times(1)).reserveStockAtomic(1L, 100L, 10);
    }

    @Test
    void testReserveStock_NotEnoughStock() {
        when(productRepository.reserveStockAtomic(1L, 100L, 60)).thenReturn(0);

        boolean result = productService.reserveStock(1L, 100L, 60);

        assertFalse(result);
        verify(productRepository, times(1)).reserveStockAtomic(1L, 100L, 60);
    }

    @Test
    void testConfirmReservation() {
        productService.confirmReservation(1L, 100L, 10);
        verify(productRepository, times(1)).confirmReservationAtomic(1L, 100L, 10);
    }

    @Test
    void testCancelReservation() {
        productService.cancelReservation(1L, 100L, 10);
        verify(productRepository, times(1)).cancelReservationAtomic(1L, 100L, 10);
    }
}
