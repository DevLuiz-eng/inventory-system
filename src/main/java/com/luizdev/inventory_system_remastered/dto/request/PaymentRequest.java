package com.luizdev.inventory_system_remastered.dto.request;
import com.luizdev.inventory_system_remastered.enums.PaymentMethod;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentRequest(

        @NotNull
        @Positive
        BigDecimal amount,

        @NotNull
        PaymentMethod paymentMethod,

        LocalDate dueDate

) {}
