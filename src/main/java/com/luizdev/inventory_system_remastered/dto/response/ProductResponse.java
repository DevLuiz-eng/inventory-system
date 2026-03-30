package com.luizdev.inventory_system_remastered.dto.response;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String brand,
        BigDecimal price,
        Boolean active
) {
}
