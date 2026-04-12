package com.luizdev.inventory_system_remastered.dto.response;

import java.time.LocalDateTime;

public record InventoryResponse(
        Long id,
        Long productId,
        String productName,
        Long warehouseId,
        String warehouseName,
        Integer quantity,
        LocalDateTime updatedAt
) {}