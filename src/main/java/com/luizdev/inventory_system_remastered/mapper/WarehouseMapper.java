package com.luizdev.inventory_system_remastered.mapper;

import com.luizdev.inventory_system_remastered.dto.request.WarehouseRequest;
import com.luizdev.inventory_system_remastered.dto.response.WarehouseResponse;
import com.luizdev.inventory_system_remastered.entity.Warehouse;

public class WarehouseMapper {

    public static Warehouse toEntity(WarehouseRequest request) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.name());
        warehouse.setLocation(request.location());
        warehouse.setActive(true);
        return warehouse;
    }

    public static WarehouseResponse toResponse(Warehouse warehouse) {
        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getLocation(),
                warehouse.getActive()
        );
    }
}