package com.money.SaveMi.Filter;

import java.math.BigDecimal;

/**
 * ExpenseFilter
 */
public record ExpenseFilter(
    String text,
    Long category,
    Long symbol,
    BigDecimal amountStart,
    BigDecimal amountFinish
) {
}
