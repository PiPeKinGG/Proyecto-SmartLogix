package com.smartlogix.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long pymeId;
    private Long userId;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String customerName;

    @NotBlank(message = "El RUT del cliente es obligatorio")
    private String customerRut;

    @Email(message = "El email del cliente debe ser valido")
    @NotBlank(message = "El email del cliente es obligatorio")
    private String customerEmail;

    @NotBlank(message = "La direccion de envio es obligatoria")
    private String shippingAddress;

    private String shippingType = "STANDARD";

    @NotNull(message = "El monto total es obligatorio")
    @Positive(message = "El monto total debe ser mayor a cero")
    private Double totalAmount;

    @Valid
    @NotEmpty(message = "Debe agregar al menos un producto")
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "El producto es obligatorio")
        private Long productId;

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        private Integer quantity;
    }

