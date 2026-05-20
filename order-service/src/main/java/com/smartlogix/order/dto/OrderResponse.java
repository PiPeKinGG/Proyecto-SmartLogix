package com.smartlogix.order.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long orderId;
    private Long pymeId;
    private Long userId;
    private String status;
    private String customerName;
    private String customerRut;
    private String customerEmail;
    private String shippingAddress;
    private String shippingType;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;

    @Data
    public static class OrderItemDto {
        private Long productId;
        private Integer quantity;
    }
}
