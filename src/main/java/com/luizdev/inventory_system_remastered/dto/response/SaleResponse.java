package com.luizdev.inventory_system_remastered.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponse(

        Long id,
        Long warehouseId,
        String warehouseName,

        Long userId,
        String userName,

        BigDecimal totalAmount,

        List<SaleItemResponse> items,
        List<PaymentResponse> payments,

        LocalDateTime createdAt

) {}
