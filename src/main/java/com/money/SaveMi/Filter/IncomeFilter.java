package com.money.SaveMi.Filter;

import java.math.BigDecimal;

/**
 * IncomeFilter
 */
public record IncomeFilter(
    String text,
    Long symbol,
    BigDecimal amountStart,
    BigDecimal amountFinish
) {
}
