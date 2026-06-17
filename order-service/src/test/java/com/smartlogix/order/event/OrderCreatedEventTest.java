package com.smartlogix.order.event;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderCreatedEventTest {

    @Test
    void testOrderItemData_AllArgsConstructorAndAccessors() {
        OrderCreatedEvent.OrderItemData item = new OrderCreatedEvent.OrderItemData(10L, 3);

        assertEquals(10L, item.getProductId());
        assertEquals(3, item.getQuantity());

        OrderCreatedEvent.OrderItemData itemNoArgs = new OrderCreatedEvent.OrderItemData();
        itemNoArgs.setProductId(10L);
        itemNoArgs.setQuantity(3);

        assertEquals(item, itemNoArgs);
        assertEquals(item.hashCode(), itemNoArgs.hashCode());
        assertNotNull(item.toString());

        OrderCreatedEvent.OrderItemData different = new OrderCreatedEvent.OrderItemData(99L, 1);
        assertNotEquals(item, different);
    }

    @Test
    void testOrderCreatedEvent_AllArgsConstructorAndAccessors() {
        OrderCreatedEvent.OrderItemData item = new OrderCreatedEvent.OrderItemData(10L, 3);

        OrderCreatedEvent event = new OrderCreatedEvent(
                1L, 100L, 7L, List.of(item), "EXPRESS"
        );

        assertEquals(1L, event.getOrderId());
        assertEquals(100L, event.getPymeId());
        assertEquals(7L, event.getUserId());
        assertEquals(1, event.getItems().size());
        assertEquals("EXPRESS", event.getShippingType());

        OrderCreatedEvent eventNoArgs = new OrderCreatedEvent();
        eventNoArgs.setOrderId(1L);
        eventNoArgs.setPymeId(100L);
        eventNoArgs.setUserId(7L);
        eventNoArgs.setItems(List.of(item));
        eventNoArgs.setShippingType("EXPRESS");

        assertEquals(event, eventNoArgs);
        assertEquals(event.hashCode(), eventNoArgs.hashCode());
        assertNotNull(event.toString());

        OrderCreatedEvent different = new OrderCreatedEvent(
                2L, 200L, 8L, List.of(item), "STANDARD"
        );
        assertNotEquals(event, different);
    }
}