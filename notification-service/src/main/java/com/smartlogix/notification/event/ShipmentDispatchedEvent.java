package com.smartlogix.notification.event;

import lombok.Data;

@Data
public class ShipmentDispatchedEvent {
    private Long orderId;
    private Long pymeId;
    private Long userId;
    private String trackingId;
    private String shippingType;

    // Getters manuales para evitar el fallo de compilación con Lombok en Docker
    public Long getUserId() {
        return userId;
    }

    public String getTrackingId() {
        return trackingId;
    }
}