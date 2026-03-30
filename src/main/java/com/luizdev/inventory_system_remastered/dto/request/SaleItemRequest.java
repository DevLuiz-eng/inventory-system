package com.luizdev.inventory_system_remastered.dto.request;
import jakarta.validation.constraints.*;

public record SaleItemRequest(

        @NotNull
        Long productId,

        @NotNull
        @Positive
        Integer quantity

) {}
