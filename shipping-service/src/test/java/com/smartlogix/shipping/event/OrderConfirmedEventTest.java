package com.smartlogix.shipping.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderConfirmedEventTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        OrderConfirmedEvent event = new OrderConfirmedEvent();
        event.setOrderId(1L);
        event.setPymeId(2L);
        event.setUserId(3L);
        event.setShippingType("STANDARD");

        assertEquals(1L, event.getOrderId());
        assertEquals(2L, event.getPymeId());
        assertEquals(3L, event.getUserId());
        assertEquals("STANDARD", event.getShippingType());
    }

    @Test
    void testAllArgsConstructor() {
        OrderConfirmedEvent event = new OrderConfirmedEvent(1L, 2L, 3L, "STANDARD");

        assertEquals(1L, event.getOrderId());
        assertEquals(2L, event.getPymeId());
        assertEquals(3L, event.getUserId());
        assertEquals("STANDARD", event.getShippingType());
    }

    @Test
    void testEqualsAndHashCode() {
        OrderConfirmedEvent event1 = new OrderConfirmedEvent(1L, 2L, 3L, "STANDARD");
        OrderConfirmedEvent event2 = new OrderConfirmedEvent(1L, 2L, 3L, "STANDARD");
        OrderConfirmedEvent event3 = new OrderConfirmedEvent(9L, 9L, 9L, "EXPRESS");

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
        assertNotEquals(event1, null);
        assertNotEquals(event1, new Object());
    }

    @Test
    void testToString() {
        OrderConfirmedEvent event = new OrderConfirmedEvent(1L, 2L, 3L, "STANDARD");
        assertNotNull(event.toString());
        assertTrue(event.toString().contains("STANDARD"));
    }
}