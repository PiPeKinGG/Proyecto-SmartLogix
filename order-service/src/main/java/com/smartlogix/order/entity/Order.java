package com.smartlogix.order.entity;

import jakarta.persistence.*;
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
    private String status; // PENDING, CONFIRMED, CANCELLED_NO_STOCK

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // --- NUEVOS CAMPOS COMERCIALES Y LOGÍSTICOS ---
    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerRut;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String shippingAddress;

    @Column(nullable = false)
    private Double totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items;
}