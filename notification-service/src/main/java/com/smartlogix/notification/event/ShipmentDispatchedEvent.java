package com.smartlogix.notification.event;

import lombok.Data;

@Data
public class ShipmentDispatchedEvent {
    private Long orderId;
    private Long pymeId;
    private Long userId;
    private String trackingId;
    private String shippingType;
}
