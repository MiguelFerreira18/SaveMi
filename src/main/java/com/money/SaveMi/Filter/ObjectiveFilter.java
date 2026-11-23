package com.money.SaveMi.Filter;

import java.math.BigDecimal;

/**
 * ObjectiveFilter
 */
public record ObjectiveFilter(
        String text,
        Long symbol,
        BigDecimal amountStart,
        BigDecimal amountFinish,
        Integer targetStart,
        Integer targetFinish) {
}
