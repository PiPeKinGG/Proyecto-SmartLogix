package com.smartlogix.shipping.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderConfirmedEvent {
    private Long orderId;
    private Long pymeId;
    private Long userId;
    private String shippingType; // EXPRESS, STANDARD, etc.
}
