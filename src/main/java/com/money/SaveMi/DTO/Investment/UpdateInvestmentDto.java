package com.money.SaveMi.DTO.Investment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateInvestmentDto(
        @NotNull @NotBlank Long id,
        @NotNull @NotBlank Long currencyId,
        @NotNull @NotBlank Long strategyTypeId,
        @NotNull @NotBlank @DecimalMin(value = "0", inclusive = false) BigDecimal amount,
        @NotNull @NotBlank String description,
        @NotNull @FutureOrPresent LocalDate date
) {
}
