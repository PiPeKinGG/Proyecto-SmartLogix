package com.smartlogix.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogix.inventory.entity.Product;
import com.smartlogix.inventory.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product sampleProduct() {
        Product p = new Product();
        p.setId(1L);
        p.setPymeId(100L);
        p.setName("Test Product");
        p.setTotalQuantity(50);
        p.setAvailableQuantity(50);
        p.setReservedQuantity(0);
        return p;
    }

    @Test
    void testGetAll() throws Exception {
        Product p = sampleProduct();
        when(productService.getAllProductsByPyme(100L)).thenReturn(List.of(p));

        mockMvc.perform(get("/products").header("pyme_id", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"));

        verify(productService, times(1)).getAllProductsByPyme(100L);
    }

    @Test
    void testGetById_Found() throws Exception {
        Product p = sampleProduct();
        when(productService.getProductById(1L, 100L)).thenReturn(Optional.of(p));

        mockMvc.perform(get("/products/1").header("pyme_id", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(productService).getProductById(1L, 100L);
    }

    @Test
    void testGetById_NotFound() throws Exception {
        when(productService.getProductById(1L, 100L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/products/1").header("pyme_id", "100"))
                .andExpect(status().isNotFound());

        verify(productService).getProductById(1L, 100L);
    }

    @Test
    void testCreate() throws Exception {
        Product toCreate = new Product();
        toCreate.setName("New Product");
        toCreate.setTotalQuantity(20);

        when(productService.createProduct(any(Product.class))).thenAnswer(invocation -> {
            Product arg = invocation.getArgument(0);
            arg.setId(2L);
            return arg;
        });

        mockMvc.perform(post("/products")
                        .header("pyme_id", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.pymeId").value(100));

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    void testUpdate_Success() throws Exception {
        Product p = sampleProduct();
        Product updateData = new Product();
        updateData.setName("Updated Product");
        updateData.setTotalQuantity(60);

        when(productService.updateProduct(eq(1L), eq(100L), any(Product.class))).thenReturn(Optional.of(p));

        mockMvc.perform(put("/products/1")
                        .header("pyme_id", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(productService).updateProduct(eq(1L), eq(100L), any(Product.class));
    }

    @Test
    void testUpdate_NotFound() throws Exception {
        Product updateData = new Product();
        updateData.setName("Updated Product");

        when(productService.updateProduct(eq(1L), eq(100L), any(Product.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/products/1")
                        .header("pyme_id", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        verify(productService).updateProduct(eq(1L), eq(100L), any(Product.class));
    }

    @Test
    void testReserve_Success() throws Exception {
        when(productService.reserveStock(1L, 100L, 10)).thenReturn(true);

        mockMvc.perform(post("/products/reserve")
                        .param("productId", "1")
                        .param("quantity", "10")
                        .header("pyme_id", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Stock reservado"));

        verify(productService).reserveStock(1L, 100L, 10);
    }

    @Test
    void testReserve_Failure() throws Exception {
        when(productService.reserveStock(1L, 100L, 60)).thenReturn(false);

        mockMvc.perform(post("/products/reserve")
                        .param("productId", "1")
                        .param("quantity", "60")
                        .header("pyme_id", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No hay stock suficiente"));

        verify(productService).reserveStock(1L, 100L, 60);
    }

    @Test
    void testConfirm() throws Exception {
        doNothing().when(productService).confirmReservation(1L, 100L, 10);

        mockMvc.perform(post("/products/confirm")
                        .param("productId", "1")
                        .param("quantity", "10")
                        .header("pyme_id", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reserva confirmada"));

        verify(productService).confirmReservation(1L, 100L, 10);
    }

    @Test
    void testCancel() throws Exception {
        doNothing().when(productService).cancelReservation(1L, 100L, 10);

        mockMvc.perform(post("/products/cancel")
                        .param("productId", "1")
                        .param("quantity", "10")
                        .header("pyme_id", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reserva cancelada"));

        verify(productService).cancelReservation(1L, 100L, 10);
    }
}

