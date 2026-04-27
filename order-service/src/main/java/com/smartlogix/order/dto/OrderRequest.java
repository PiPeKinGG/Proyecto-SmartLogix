package com.smartlogix.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long pymeId;
    private Long userId;
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
    }
}
