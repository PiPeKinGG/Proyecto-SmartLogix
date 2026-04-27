package com.smartlogix.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderResponse {
    private Long orderId;
    private String status;
    private List<OrderItemDto> items;

    @Data
    public static class OrderItemDto {
        private Long productId;
        private Integer quantity;
    }
}
