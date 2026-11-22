package com.money.SaveMi.Filter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * InvestmentFilter
 */
public record InvestmentFilter(
        String text,
        Long symbol,
        Long strategy,
        BigDecimal amountStart,
        BigDecimal amountFinish,
        LocalDate dateStart,
        LocalDate dateFinish) {
}
