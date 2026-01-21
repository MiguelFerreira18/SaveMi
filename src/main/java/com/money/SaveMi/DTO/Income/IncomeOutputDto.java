package com.money.SaveMi.DTO.Income;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IncomeOutputDto(
        Long id,
        String symbol,
        String description,
        BigDecimal amount,
        String userId,
        LocalDate date
) {
}
