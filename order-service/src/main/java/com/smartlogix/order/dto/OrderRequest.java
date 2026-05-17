package com.smartlogix.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long pymeId;
    private Long userId;
    
    // --- NUEVOS CAMPOS ---
    private String customerName;
    private String customerRut;
    private String customerEmail;
    private String shippingAddress;
    private Double totalAmount; 
    
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
    }
}