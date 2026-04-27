package com.smartlogix.notification.factory;

import com.smartlogix.notification.service.EmailNotificationService;
import com.smartlogix.notification.service.NotificationService;
import com.smartlogix.notification.service.SmsNotificationService;

public class NotificationFactory {
    public static NotificationService getNotificationService(String shippingType) {
        // Ejemplo: Si es EXPRESS, notifica por SMS, si es STANDARD, por email
        if ("EXPRESS".equalsIgnoreCase(shippingType)) {
            return new SmsNotificationService();
        } else {
            return new EmailNotificationService();
        }
    }
}
