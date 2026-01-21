package com.money.SaveMi.DTO.Currency;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SaveCurrencyDto(
        @NotBlank @NotNull String name,
        @Size(min = 3, max = 3) String symbol
) {
}
