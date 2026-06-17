package com.smartlogix.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pymeId;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Column(nullable = false)
    private String name;

    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    @Column(nullable = false)
    private Integer availableQuantity;

    @Min(value = 0, message = "La cantidad reservada no puede ser negativa")
    @Column(nullable = false)
    private Integer reservedQuantity;

    @Min(value = 0, message = "La cantidad total no puede ser negativa")
    @Column(nullable = false)
    private Integer totalQuantity;

    @Min(value = 0, message = "El precio no puede ser negativo")
    @Column(nullable = false)
    private Double price;
}