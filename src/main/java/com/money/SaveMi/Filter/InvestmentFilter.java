package com.money.SaveMi.Filter;

import java.math.BigDecimal;

/**
 * InvestmentFilter
 */
public record InvestmentFilter(
        String text,
        Long symbol,
        Long strategy,
        BigDecimal amountStart,
        BigDecimal amountFinish) {
}
