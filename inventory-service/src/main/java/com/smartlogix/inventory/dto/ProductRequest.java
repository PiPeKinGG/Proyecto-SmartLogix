package com.smartlogix.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "La cantidad inicial es obligatoria")
    @Min(value = 1, message = "Debe registrar al menos 1 producto")
    private Integer totalQuantity;
}