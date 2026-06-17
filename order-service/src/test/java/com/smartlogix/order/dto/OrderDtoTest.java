package com.smartlogix.order.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderDtoTest {

    @Test
    void testOrderRequestAndInnerClass() {
        // 1. Instanciar y probar Setters
        OrderRequest request = new OrderRequest();
        request.setPymeId(1L);
        request.setUserId(2L);
        request.setCustomerName("Juan");
        request.setCustomerRut("11111111-1");
        request.setCustomerEmail("test@test.com");
        request.setShippingAddress("Calle Falsa 123");
        request.setShippingType("STANDARD");
        request.setTotalAmount(15000.0);

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(10L);
        item.setQuantity(2);
        request.setItems(List.of(item));

        // 2. Probar Getters
        assertEquals(1L, request.getPymeId());
        assertEquals(2L, request.getUserId());
        assertEquals("Juan", request.getCustomerName());
        assertEquals("11111111-1", request.getCustomerRut());
        assertEquals("test@test.com", request.getCustomerEmail());
        assertEquals("Calle Falsa 123", request.getShippingAddress());
        assertEquals("STANDARD", request.getShippingType());
        assertEquals(15000.0, request.getTotalAmount());
        assertEquals(1, request.getItems().size());
        assertEquals(10L, item.getProductId());
        assertEquals(2, item.getQuantity());

        // 3. Probar métodos generados por Lombok (toString, hashCode, equals)
        assertNotNull(request.toString());
        assertNotNull(item.toString());
        assertNotEquals(0, request.hashCode());
        assertNotEquals(0, item.hashCode());

        OrderRequest request2 = new OrderRequest();
        assertNotEquals(request, request2);
        assertEquals(request, request);

        OrderRequest.OrderItemRequest item2 = new OrderRequest.OrderItemRequest();
        assertNotEquals(item, item2);
        assertEquals(item, item);
    }

    @Test
    void testOrderResponseAndInnerClass() {
        // 1. Instanciar y probar Setters
        OrderResponse response = new OrderResponse();
        response.setId(1L);
        response.setOrderId(100L);
        response.setPymeId(2L);
        response.setUserId(3L);
        response.setStatus("PENDING");
        response.setCustomerName("Pedro");
        response.setCustomerRut("22222222-2");
        response.setCustomerEmail("pedro@test.com");
        response.setShippingAddress("Avenida 456");
        response.setShippingType("EXPRESS");
        response.setTotalAmount(20000.0);
        response.setCreatedAt(LocalDateTime.now());

        OrderResponse.OrderItemDto item = new OrderResponse.OrderItemDto();
        item.setProductId(20L);
        item.setQuantity(5);
        response.setItems(List.of(item));

        // 2. Probar Getters
        assertEquals(1L, response.getId());
        assertEquals(100L, response.getOrderId());
        assertEquals("PENDING", response.getStatus());
        assertEquals(20L, item.getProductId());
        assertEquals(5, item.getQuantity());

        // 3. Probar métodos generados por Lombok
        assertNotNull(response.toString());
        assertNotNull(item.toString());
        assertNotEquals(0, response.hashCode());
        assertNotEquals(0, item.hashCode());

        OrderResponse response2 = new OrderResponse();
        assertNotEquals(response, response2);
        assertEquals(response, response);

        OrderResponse.OrderItemDto item2 = new OrderResponse.OrderItemDto();
        assertNotEquals(item, item2);
        assertEquals(item, item);
    }
}