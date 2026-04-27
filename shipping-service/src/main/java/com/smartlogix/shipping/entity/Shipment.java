package com.smartlogix.shipping.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "shipments")
@Getter
@Setter
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long pymeId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String shippingType; // EXPRESS, STANDARD, etc.

    @Column(nullable = false)
    private Double cost;

    @Column(nullable = false)
    private Integer estimatedDays;

    @Column(nullable = false, unique = true)
    private String trackingId;
}
