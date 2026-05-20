package com.smartlogix.order.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderExceptionTest {
    @Test
    void testOrderExceptionMessage() {
        OrderException ex = new OrderException("Order not found");
        assertEquals("Order not found", ex.getMessage());
    }
}
