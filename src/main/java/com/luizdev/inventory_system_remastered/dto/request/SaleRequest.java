package com.luizdev.inventory_system_remastered.dto.request;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record SaleRequest(

        @NotNull
        Long warehouseId,

        @NotNull
        Long userId,

        @NotEmpty
        @Valid
        List<SaleItemRequest> items,

        @NotEmpty
        @Valid
        List<PaymentRequest> payments

) {}
