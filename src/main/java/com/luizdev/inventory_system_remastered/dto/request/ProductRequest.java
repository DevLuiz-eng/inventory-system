package com.luizdev.inventory_system_remastered.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank
        String name,

        String description,

        @NotBlank
        String brand,

        @NotNull
        @Positive
        BigDecimal price
) {
}
