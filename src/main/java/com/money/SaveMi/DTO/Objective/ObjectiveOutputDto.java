package com.money.SaveMi.DTO.Objective;

import java.math.BigDecimal;

public record ObjectiveOutputDto(
        Long id,
        String symbol,
        String description,
        BigDecimal amount,
        String userId,
        int target
) {
}
