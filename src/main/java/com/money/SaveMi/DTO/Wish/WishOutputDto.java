package com.money.SaveMi.DTO.Wish;


import java.math.BigDecimal;
import java.time.LocalDate;

public record WishOutputDto(
        Long id,
        String symbol,
        String description,
        BigDecimal amount,
        String userId,
        LocalDate date
) {

}
