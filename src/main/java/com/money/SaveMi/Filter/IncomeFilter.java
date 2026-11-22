package com.money.SaveMi.Filter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * IncomeFilter
 */
public record IncomeFilter(
    String text,
    Long symbol,
    BigDecimal amountStart,
    BigDecimal amountFinish,
    LocalDate dateStart,
    LocalDate dateFinish
) {
}
