package com.smartlogix.notification.service;

import com.smartlogix.notification.event.ShipmentDispatchedEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class NotificationServiceTest {

    @Test
    void testEmailNotificationService() {
        // Given
        EmailNotificationService emailService = new EmailNotificationService();
        ShipmentDispatchedEvent event = new ShipmentDispatchedEvent();
        event.setOrderId(1L);
        event.setUserId(2L);
        event.setTrackingId("TRK-12345");
        event.setShippingType("STANDARD");

        // When & Then
        // Simple assertion as the real method only prints to console
        assertDoesNotThrow(() -> emailService.notify(event));
    }

    @Test
    void testSmsNotificationService() {
        // Given
        SmsNotificationService smsService = new SmsNotificationService();
        ShipmentDispatchedEvent event = new ShipmentDispatchedEvent();
        event.setOrderId(2L);
        event.setUserId(3L);
        event.setTrackingId("TRK-67890");
        event.setShippingType("EXPRESS");

        // When & Then
        assertDoesNotThrow(() -> smsService.notify(event));
    }
}