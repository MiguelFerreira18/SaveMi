package com.money.SaveMi.Filter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ExpenseFilter
 */
public record ExpenseFilter(
    String text,
    Long category,
    Long symbol,
    BigDecimal amountStart,
    BigDecimal amountFinish,
    LocalDate dateStart,
    LocalDate dateFinish
) {
}
