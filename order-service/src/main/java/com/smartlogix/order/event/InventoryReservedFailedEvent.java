package com.smartlogix.order.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReservedFailedEvent {
    private Long orderId;
    private Long pymeId;
}
