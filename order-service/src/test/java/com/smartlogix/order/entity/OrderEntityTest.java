package com.smartlogix.order.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderEntityTest {
    @Test
    void testOrderEntitySettersAndGetters() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus("CREATED");
        assertEquals(1L, order.getId());
        assertEquals("CREATED", order.getStatus());
    }
}
