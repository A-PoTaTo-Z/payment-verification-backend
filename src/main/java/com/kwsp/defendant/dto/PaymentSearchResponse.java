package com.kwsp.defendant.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentSearchResponse(
        String paymentReference,
        String paymentStatus,
        BigDecimal amount,
        LocalDate dueDate
) {
}