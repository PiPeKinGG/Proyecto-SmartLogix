package com.smartlogix.order.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderDtoTest {
    @Test
    void testOrderResponseSettersAndGetters() {
        OrderResponse response = new OrderResponse();
        response.setId(1L);
        response.setStatus("CREATED");
        
        assertEquals(1L, response.getId());
        assertEquals("CREATED", response.getStatus());
    }
}
