package com.smartlogix.notification.service;

import com.smartlogix.notification.event.ShipmentDispatchedEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EmailNotificationServiceTest {
    @Test
    void testSendEmail() {
        EmailNotificationService service = new EmailNotificationService();
        ShipmentDispatchedEvent event = new ShipmentDispatchedEvent();
        event.setUserId(1L);
        event.setTrackingId("TRK-123");
        
        assertDoesNotThrow(() -> service.notify(event));
    }
}