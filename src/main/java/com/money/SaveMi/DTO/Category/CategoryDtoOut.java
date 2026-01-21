package com.money.SaveMi.DTO.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryDtoOut(
        @NotBlank @NotNull Long id,
        @NotBlank @NotNull String name,
        @NotBlank @NotNull String description) {

}
