package com.smartlogix.notification.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShipmentDispatchedEventTest {
    @Test
    void testEventFields() {
        ShipmentDispatchedEvent event = new ShipmentDispatchedEvent();
        event.setOrderId(1L);
        event.setUserId(2L);
        assertEquals(1L, event.getOrderId());
        assertEquals(2L, event.getUserId());
    }
}
