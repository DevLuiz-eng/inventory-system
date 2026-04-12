package com.luizdev.inventory_system_remastered.dto.request;

import com.luizdev.inventory_system_remastered.enums.TypeOfStockMovement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockMovementRequest(

        @NotNull
        Long productId,

        @NotNull
        Long warehouseId,

        @NotNull
        Long createdBy,

        @NotNull
        TypeOfStockMovement type,

        @NotNull
        @Positive
        Integer quantity,

        @NotBlank
        String reason

) {}