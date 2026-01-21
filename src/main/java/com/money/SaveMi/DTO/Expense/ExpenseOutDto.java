package com.money.SaveMi.DTO.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseOutDto(
        Long id,
        String category,
        String symbol,
        String description,
        BigDecimal amount,
        String userId,
        LocalDate date
) {
}
