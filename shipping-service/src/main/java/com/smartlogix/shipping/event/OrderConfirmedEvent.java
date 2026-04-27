package com.smartlogix.shipping.event;

import lombok.Data;

@Data
public class OrderConfirmedEvent {
    private Long orderId;
    private Long pymeId;
    private Long userId;
    private String shippingType; // EXPRESS, STANDARD, etc.
}
