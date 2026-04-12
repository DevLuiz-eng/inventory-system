package com.luizdev.inventory_system_remastered.dto.response;

import com.luizdev.inventory_system_remastered.enums.TypeOfStockMovement;

import java.time.LocalDateTime;

public record StockMovementResponse(

        Long id,
        Long productId,
        String productName,
        Long warehouseId,
        String warehouseName,
        Long createdById,
        String createdByName,
        TypeOfStockMovement type,
        Integer quantity,
        String reason,
        LocalDateTime createdAt

) {}