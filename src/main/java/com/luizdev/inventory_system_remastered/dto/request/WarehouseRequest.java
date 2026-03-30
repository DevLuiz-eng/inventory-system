package com.luizdev.inventory_system_remastered.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WarehouseRequest(
        @NotBlank
        String name,

        @NotBlank
        String location
) {
}
