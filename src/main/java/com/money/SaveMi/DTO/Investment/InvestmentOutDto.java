package com.money.SaveMi.DTO.Investment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentOutDto(
        Long id,
        String strategyType,
        String symbol,
        String description,
        BigDecimal amount,
        String userId,
        LocalDate date
) {
}
