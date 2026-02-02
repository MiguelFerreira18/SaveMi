package com.money.SaveMi.DTO.Wish;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaveWishDto(
        @NotNull @NotBlank  Long currencyId,
        @NotNull @NotBlank String description,
        @NotNull @NotBlank @DecimalMin(value = "0", inclusive = false) BigDecimal amount,
        @NotNull @FutureOrPresent LocalDate date
) {

}
