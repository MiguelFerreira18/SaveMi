package com.money.SaveMi.DTO.StrategyType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateStrategyType(
        @NotNull @NotBlank long id,
        @NotNull @NotBlank @Size(max = 50) String name,
        @NotNull @NotBlank @Size(max = 255) String description
) {
}
