package com.smartlogix.notification.service;

import com.smartlogix.notification.event.ShipmentDispatchedEvent;

public interface NotificationService {
    void notify(ShipmentDispatchedEvent event);
}
