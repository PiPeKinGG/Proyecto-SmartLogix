package com.smartlogix.shipping.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "shipments",
        uniqueConstraints = @UniqueConstraint(name = "uk_shipment_order_pyme", columnNames = {"order_id", "pyme_id"})
)
@Getter
@Setter
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "pyme_id", nullable = false)
    private Long pymeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "shipping_type", nullable = false)
    private String shippingType; // EXPRESS, STANDARD, etc.

    @Column(nullable = false)
    private Double cost;

    @Column(name = "estimated_days", nullable = false)
    private Integer estimatedDays;

    @Column(name = "tracking_id", nullable = false, unique = true)
    private String trackingId;

    @Column
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (status == null || status.isBlank()) {
            status = "DISPATCHED";
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
