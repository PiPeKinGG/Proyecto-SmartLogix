package com.smartlogix.notification.service;

import com.smartlogix.notification.event.ShipmentDispatchedEvent;

public class SmsNotificationService implements NotificationService {
    @Override
    public void notify(ShipmentDispatchedEvent event) {
        System.out.println("[SMS] Enviando SMS a usuario " + event.getUserId() + " con tracking " + event.getTrackingId());
    }
}