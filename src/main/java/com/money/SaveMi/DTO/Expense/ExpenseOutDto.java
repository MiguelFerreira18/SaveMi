package com.money.SaveMi.DTO.Expense;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        public ExpenseOutDto{
                if (amount!= null) {
                    amount = amount.setScale(2,RoundingMode.HALF_UP);
                }
        }
}
