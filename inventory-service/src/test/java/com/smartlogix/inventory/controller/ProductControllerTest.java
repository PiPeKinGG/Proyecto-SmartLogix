package com.smartlogix.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogix.inventory.dto.ProductRequest;
import com.smartlogix.inventory.dto.ProductResponse;
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

    private ProductResponse sampleResponse() {
        ProductResponse r = new ProductResponse();
        r.setId(1L);
        r.setPymeId(100L);
        r.setName("Test Product");
        r.setTotalQuantity(50);
        r.setAvailableQuantity(50);
        r.setReservedQuantity(0);
        return r;
    }

    private ProductRequest sampleRequest() {
        ProductRequest r = new ProductRequest();
        r.setName("Test Product");
        r.setTotalQuantity(50);
        return r;
    }

    @Test
    void testGetAll() throws Exception {
        when(productService.getAllProductsByPyme(100L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/products").header("pyme_id", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"));

        verify(productService, times(1)).getAllProductsByPyme(100L);
    }

    @Test
    void testGetById_Found() throws Exception {
        when(productService.getProductById(1L, 100L)).thenReturn(Optional.of(sampleResponse()));

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
        ProductResponse created = sampleResponse();
        created.setId(2L);
        when(productService.createProduct(any(ProductRequest.class), eq(100L))).thenReturn(created);

        mockMvc.perform(post("/products")
                        .header("pyme_id", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));

        verify(productService).createProduct(any(ProductRequest.class), eq(100L));
    }

    @Test
    void testUpdate_Success() throws Exception {
        when(productService.updateProduct(eq(1L), eq(100L), any(ProductRequest.class))).thenReturn(Optional.of(sampleResponse()));

        mockMvc.perform(put("/products/1")
                        .header("pyme_id", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(productService).updateProduct(eq(1L), eq(100L), any(ProductRequest.class));
    }

    @Test
    void testUpdate_NotFound() throws Exception {
        when(productService.updateProduct(eq(1L), eq(100L), any(ProductRequest.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/products/1")
                        .header("pyme_id", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isNotFound());

        verify(productService).updateProduct(eq(1L), eq(100L), any(ProductRequest.class));
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
