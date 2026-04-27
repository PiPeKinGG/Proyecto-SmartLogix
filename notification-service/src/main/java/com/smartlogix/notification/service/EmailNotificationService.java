package com.smartlogix.notification.service;

import com.smartlogix.notification.event.ShipmentDispatchedEvent;

public class EmailNotificationService implements NotificationService {
    @Override
    public void notify(ShipmentDispatchedEvent event) {
        // Aquí iría la lógica real de envío de email
        System.out.println("[EMAIL] Enviando email a usuario " + event.getUserId() + " con tracking " + event.getTrackingId());
    }
}
