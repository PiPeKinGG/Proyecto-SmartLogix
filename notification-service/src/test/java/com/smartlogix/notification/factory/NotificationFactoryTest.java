package com.smartlogix.notification.factory;

import com.smartlogix.notification.service.EmailNotificationService;
import com.smartlogix.notification.service.NotificationService;
import com.smartlogix.notification.service.SmsNotificationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class NotificationFactoryTest {

    @Test
    void testGetNotificationService_Express() {
        // When
        NotificationService service = NotificationFactory.getNotificationService("EXPRESS");

        // Then
        assertInstanceOf(SmsNotificationService.class, service);
    }

    @Test
    void testGetNotificationService_Express_CaseInsensitive() {
        // When
        NotificationService service = NotificationFactory.getNotificationService("express");

        // Then
        assertInstanceOf(SmsNotificationService.class, service);
    }

    @Test
    void testGetNotificationService_Standard() {
        // When
        NotificationService service = NotificationFactory.getNotificationService("STANDARD");

        // Then
        assertInstanceOf(EmailNotificationService.class, service);
    }

    @Test
    void testGetNotificationService_NullOrUnknown() {
        // When
        NotificationService serviceNull = NotificationFactory.getNotificationService(null);
        NotificationService serviceUnknown = NotificationFactory.getNotificationService("UNKNOWN_TYPE");

        // Then
        assertInstanceOf(EmailNotificationService.class, serviceNull);
        assertInstanceOf(EmailNotificationService.class, serviceUnknown);
    }
}