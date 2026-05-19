package com.smartlogix.shipping.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShipmentResponse {
    private Long id;
    private Long orderId;
    private Long pymeId;
    private Long userId;
    private String shippingType;
    private Double cost;
    private Integer estimatedDays;
    private String trackingId;
    private String status;
    private LocalDateTime createdAt;
}
