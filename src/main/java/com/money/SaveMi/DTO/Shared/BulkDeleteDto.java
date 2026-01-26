package com.money.SaveMi.DTO.Shared;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BulkDeleteDto(
        @NotEmpty @NotNull List<Long> ids
) {
}
