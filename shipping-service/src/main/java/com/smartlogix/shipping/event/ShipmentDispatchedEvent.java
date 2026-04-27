package com.smartlogix.shipping.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentDispatchedEvent {
    private Long orderId;
    private Long pymeId;
    private Long userId;
    private String trackingId;
    private String shippingType;
}
