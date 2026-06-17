package com.smartlogix.order.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RemainingEventsTest {

    @Test
    void testOrderConfirmedEvent_AllArgsConstructorAndAccessors() {
        OrderConfirmedEvent event = new OrderConfirmedEvent(1L, 100L, 7L, "EXPRESS");

        assertEquals(1L, event.getOrderId());
        assertEquals(100L, event.getPymeId());
        assertEquals(7L, event.getUserId());
        assertEquals("EXPRESS", event.getShippingType());

        OrderConfirmedEvent eventNoArgs = new OrderConfirmedEvent();
        eventNoArgs.setOrderId(1L);
        eventNoArgs.setPymeId(100L);
        eventNoArgs.setUserId(7L);
        eventNoArgs.setShippingType("EXPRESS");

        assertEquals(event, eventNoArgs);
        assertEquals(event.hashCode(), eventNoArgs.hashCode());
        assertNotNull(event.toString());

        OrderConfirmedEvent different = new OrderConfirmedEvent(2L, 200L, 8L, "STANDARD");
        assertNotEquals(event, different);
    }

    @Test
    void testInventoryReservedSuccessEvent_AllArgsConstructorAndAccessors() {
        InventoryReservedSuccessEvent event = new InventoryReservedSuccessEvent(1L, 100L);

        assertEquals(1L, event.getOrderId());
        assertEquals(100L, event.getPymeId());

        InventoryReservedSuccessEvent eventNoArgs = new InventoryReservedSuccessEvent();
        eventNoArgs.setOrderId(1L);
        eventNoArgs.setPymeId(100L);

        assertEquals(event, eventNoArgs);
        assertEquals(event.hashCode(), eventNoArgs.hashCode());
        assertNotNull(event.toString());

        InventoryReservedSuccessEvent different = new InventoryReservedSuccessEvent(2L, 200L);
        assertNotEquals(event, different);
    }

    @Test
    void testInventoryReservedFailedEvent_AllArgsConstructorAndAccessors() {
        InventoryReservedFailedEvent event = new InventoryReservedFailedEvent(1L, 100L);

        assertEquals(1L, event.getOrderId());
        assertEquals(100L, event.getPymeId());

        InventoryReservedFailedEvent eventNoArgs = new InventoryReservedFailedEvent();
        eventNoArgs.setOrderId(1L);
        eventNoArgs.setPymeId(100L);

        assertEquals(event, eventNoArgs);
        assertEquals(event.hashCode(), eventNoArgs.hashCode());
        assertNotNull(event.toString());

        InventoryReservedFailedEvent different = new InventoryReservedFailedEvent(2L, 200L);
        assertNotEquals(event, different);
    }
}