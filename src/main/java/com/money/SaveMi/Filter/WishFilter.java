package com.money.SaveMi.Filter;

import java.math.BigDecimal;

/**
 * WishFilter
 */
public record WishFilter(
    String text,
    Long symbol,
    BigDecimal amountStart,
    BigDecimal amountFinish
) {
}
