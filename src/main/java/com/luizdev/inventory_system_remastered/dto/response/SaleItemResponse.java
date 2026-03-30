package com.luizdev.inventory_system_remastered.dto.response;

import java.math.BigDecimal;

public record SaleItemResponse(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
