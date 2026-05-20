package com.smartlogix.order.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
    private Long orderId;
    private Long pymeId;
    private Long userId;
    private List<OrderItemData> items;
    private String shippingType;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItemData {
        private Long productId;
        private Integer quantity;
    }
}
