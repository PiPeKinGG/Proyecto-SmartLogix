package com.smartlogix.shipping.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShipmentDispatchedEventTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        ShipmentDispatchedEvent event = new ShipmentDispatchedEvent();
        event.setOrderId(1L);
        event.setPymeId(2L);
        event.setUserId(3L);
        event.setTrackingId("TRACK-123");
        event.setShippingType("EXPRESS");

        assertEquals(1L, event.getOrderId());
        assertEquals(2L, event.getPymeId());
        assertEquals(3L, event.getUserId());
        assertEquals("TRACK-123", event.getTrackingId());
        assertEquals("EXPRESS", event.getShippingType());
    }

    @Test
    void testAllArgsConstructor() {
        ShipmentDispatchedEvent event = new ShipmentDispatchedEvent(1L, 2L, 3L, "TRACK-123", "EXPRESS");

        assertEquals(1L, event.getOrderId());
        assertEquals(2L, event.getPymeId());
        assertEquals(3L, event.getUserId());
        assertEquals("TRACK-123", event.getTrackingId());
        assertEquals("EXPRESS", event.getShippingType());
    }

    @Test
    void testEqualsAndHashCode() {
        ShipmentDispatchedEvent event1 = new ShipmentDispatchedEvent(1L, 2L, 3L, "TRACK-123", "EXPRESS");
        ShipmentDispatchedEvent event2 = new ShipmentDispatchedEvent(1L, 2L, 3L, "TRACK-123", "EXPRESS");
        ShipmentDispatchedEvent event3 = new ShipmentDispatchedEvent(9L, 9L, 9L, "OTHER", "STANDARD");

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
        assertNotEquals(event1, null);
        assertNotEquals(event1, new Object());
    }

    @Test
    void testToString() {
        ShipmentDispatchedEvent event = new ShipmentDispatchedEvent(1L, 2L, 3L, "TRACK-123", "EXPRESS");
        assertNotNull(event.toString());
        assertTrue(event.toString().contains("TRACK-123"));
    }
}