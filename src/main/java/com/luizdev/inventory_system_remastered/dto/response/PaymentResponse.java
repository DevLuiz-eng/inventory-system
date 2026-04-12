package com.luizdev.inventory_system_remastered.dto.response;
import com.luizdev.inventory_system_remastered.enums.PaymentMethod;
import com.luizdev.inventory_system_remastered.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentResponse(

        Long id,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        LocalDate dueDate,
        LocalDateTime paidAt,
        LocalDateTime createdAt

) {}
