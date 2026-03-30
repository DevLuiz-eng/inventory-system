package com.luizdev.inventory_system_remastered.dto.response;

public record WarehouseResponse(
        Long id,
        String name,
        String location,
        Boolean active
) {
}
