package com.luizdev.inventory_system_remastered.mapper;

import com.luizdev.inventory_system_remastered.dto.response.InventoryResponse;
import com.luizdev.inventory_system_remastered.entity.Inventory;

public class InventoryMapper {

    public static InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getProduct().getId(),
                inventory.getProduct().getName(),
                inventory.getWarehouse().getId(),
                inventory.getWarehouse().getName(),
                inventory.getQuantity(),
                inventory.getUpdatedAt()
        );
    }
}