package com.money.SaveMi.DTO.Income;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateIncomeDto(
        @NotNull @NotBlank Long id,
        @NotNull @NotBlank Long currencyId,
        @NotNull @NotBlank @DecimalMin(value = "0", inclusive = false) BigDecimal amount,
        @NotNull @NotBlank @Size(max = 255) String description,
        @NotNull @FutureOrPresent LocalDate date
) {
}
