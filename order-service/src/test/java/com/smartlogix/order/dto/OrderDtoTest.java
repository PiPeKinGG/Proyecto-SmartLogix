package com.smartlogix.order.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderDtoTest {
    @Test
    void testOrderDtoSettersAndGetters() {
        OrderDto dto = new OrderDto();
        dto.setId(1L);
        dto.setStatus("CREATED");
        assertEquals(1L, dto.getId());
        assertEquals("CREATED", dto.getStatus());
    }
}
