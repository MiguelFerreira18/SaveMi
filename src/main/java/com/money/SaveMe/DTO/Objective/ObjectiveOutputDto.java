package com.money.SaveMe.DTO.Objective;

import jakarta.validation.constraints.*;

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
