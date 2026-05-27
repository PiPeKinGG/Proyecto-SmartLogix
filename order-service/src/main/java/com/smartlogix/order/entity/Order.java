package com.smartlogix.order.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pymeId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String status; 

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerRut;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String shippingAddress;

    @Column
    private String shippingType;

    @Column(nullable = false)
    private Double totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items;

    @PrePersist
    void prePersist() {
        if (status == null || status.isBlank()) {
            status = "PENDING";
        }
        if (shippingType == null || shippingType.isBlank()) {
            shippingType = "STANDARD";
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
