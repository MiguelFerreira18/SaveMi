package com.money.SaveMi.Filter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * WishFilter
 */
public record WishFilter(
    String text,
    Long symbol,
    BigDecimal amountStart,
    BigDecimal amountFinish,
    LocalDate dateStart,
    LocalDate dateFinish
) {
}
