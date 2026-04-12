package com.luizdev.inventory_system_remastered.mapper;

import com.luizdev.inventory_system_remastered.dto.request.StockMovementRequest;
import com.luizdev.inventory_system_remastered.dto.response.StockMovementResponse;
import com.luizdev.inventory_system_remastered.entity.Product;
import com.luizdev.inventory_system_remastered.entity.StockMovement;
import com.luizdev.inventory_system_remastered.entity.User;
import com.luizdev.inventory_system_remastered.entity.Warehouse;

public class StockMovementMapper {

    public static StockMovement toEntity(StockMovementRequest request,
                                         Product product,
                                         Warehouse warehouse,
                                         User user) {
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setCreatedBy(user);
        movement.setType(request.type());
        movement.setQuantity(request.quantity());
        movement.setReason(request.reason());

        return movement;
    }

    public static StockMovementResponse toResponse(StockMovement movement) {
        return new StockMovementResponse(
                movement.getId(),
                movement.getProduct().getId(),
                movement.getProduct().getName(),
                movement.getWarehouse().getId(),
                movement.getWarehouse().getName(),
                movement.getCreatedBy().getId(),
                movement.getCreatedBy().getName(),
                movement.getType(),
                movement.getQuantity(),
                movement.getReason(),
                movement.getCreatedAt()
        );
    }
}