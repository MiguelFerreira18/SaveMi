package com.money.SaveMe.DTO.Objective;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateObjectiveDto(
        @NotNull @NotBlank Long id,
        @NotNull @NotBlank Long currencyId,
        @DecimalMin(value = "0", inclusive = false) BigDecimal amount,
        @NotNull @NotBlank @Size(max = 255) String description,
        @NotNull @Min(1900) @Max(2100) int target

) {
}
