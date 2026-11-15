package com.money.SaveMi.Integration.Service;

import com.money.SaveMi.DTO.Expense.SaveExpenseDto;
import com.money.SaveMi.DTO.Income.SaveIncomeDto;
import com.money.SaveMi.DTO.Income.UpdateIncomeDto;
import com.money.SaveMi.Integration.BaseTest;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Expense;
import com.money.SaveMi.Model.Income;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.*;
import com.money.SaveMi.Service.IncomeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IncomeServiceTest extends BaseTest {
    @Autowired
    private IncomeService incomeService;

    @Autowired
    private IncomeRepo incomeRepo;

    @Autowired
    private CurrencyRepo currencyRepo;
    @Autowired
    private UserRepo userRepo;

    private Currency testCurrency;
    private User testUser;

    @BeforeEach
    void setUp() {
        incomeRepo.deleteAll();
        currencyRepo.deleteAll();
        userRepo.deleteAll();

        testUser = new User("test@example.com", "testuser", "Password123!");
        testUser = userRepo.save(testUser);

        testCurrency = new Currency(testUser, "euro", "EUR");
        testCurrency = currencyRepo.save(testCurrency);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("uuid", testUser.getId())
                .claim("email", testUser.getEmail())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @ParameterizedTest
    @CsvSource({
            "testWish1, 12.10",
            "testWish2, 12030.60",
            "testWish3, 20000.00",
    })
    void testSaveIncome(String description, BigDecimal amount) {
        SaveIncomeDto saveIncomeDto = new SaveIncomeDto(testCurrency.getId(),  amount,description, LocalDate.now().plusDays(1));

        Income income = incomeService.saveIncome(saveIncomeDto);

        assertNotNull(income.getId());
        assertEquals(description, income.getDescription());
        assertEquals(amount, income.getAmount());
        assertEquals(testCurrency.getId(), income.getCurrency().getId());
        assertEquals(testUser.getId(), income.getUser().getId());

        Optional<Income> found = incomeRepo.findById(income.getId());

        assertTrue(found.isPresent());
        assertEquals(description, found.get().getDescription());
        assertEquals(amount, found.get().getAmount());
        assertEquals(testCurrency.getId(), income.getCurrency().getId());
    }

    @Test
    void testGetAllIncomes() {
        SaveIncomeDto firstSaveIncomeDto = new SaveIncomeDto(testCurrency.getId(), BigDecimal.valueOf(12.1),"testIncome1", LocalDate.now().plusDays(1));
        SaveIncomeDto secondSaveIncomeDto = new SaveIncomeDto(testCurrency.getId(), BigDecimal.valueOf(133000.1), "testIncome2", LocalDate.now().plusDays(1));
        SaveIncomeDto thirdSaveIncomeDto = new SaveIncomeDto(testCurrency.getId(),  BigDecimal.valueOf(2000),"testIncome3", LocalDate.now().plusDays(1));

        incomeService.saveIncome(firstSaveIncomeDto);
        incomeService.saveIncome(secondSaveIncomeDto);
        incomeService.saveIncome(thirdSaveIncomeDto);

        List<Income> incomes = StreamSupport.stream(incomeService.getAllIncomeByUserId(Optional.empty()).spliterator(), false).toList();

        assertEquals(3, incomes.size());
        assertTrue(incomes.stream().anyMatch(w -> w.getDescription().equals("testIncome1")));
        assertTrue(incomes.stream().anyMatch(w -> w.getDescription().equals("testIncome2")));
        assertTrue(incomes.stream().anyMatch(w -> w.getDescription().equals("testIncome3")));
        assertFalse(incomes.stream().anyMatch(w -> w.getDescription().equals("testIncome4")));
        assertEquals(1, incomes.stream().filter(w -> w.getDescription().equals("testIncome1")).count());
        assertEquals(1, incomes.stream().filter(w -> w.getDescription().equals("testIncome2")).count());
        assertEquals(1, incomes.stream().filter(w -> w.getDescription().equals("testIncome3")).count());
    }
    @ParameterizedTest
    @CsvSource({
            "2025-01",
            "2026-06",
            "2026-12",
    })
    void testGetAllIncomeWithFilter(YearMonth month) {
        SaveIncomeDto firstSaveIncomeDto = new SaveIncomeDto(testCurrency.getId(), BigDecimal.valueOf(12.1), "testIncome1", month.atDay(1).plusMonths(1));
        SaveIncomeDto secondSaveIncomeDto = new SaveIncomeDto(testCurrency.getId(), BigDecimal.valueOf(133000.1), "testIncome2", month.atDay(1).minusMonths(1));
        SaveIncomeDto thirdSaveIncomeDto = new SaveIncomeDto(testCurrency.getId(), BigDecimal.valueOf(2000), "testIncome3", month.atDay(1));

        incomeService.saveIncome(firstSaveIncomeDto);
        incomeService.saveIncome(secondSaveIncomeDto);
        incomeService.saveIncome(thirdSaveIncomeDto);

        List<Income> incomes = StreamSupport.stream(incomeService.getAllIncomeByUserId(Optional.of(month)).spliterator(), false).toList();

        assertEquals(1, incomes.size());
        assertTrue(incomes.stream().anyMatch(w -> w.getDescription().equals("testIncome3")));
        assertFalse(incomes.stream().anyMatch(w -> w.getDescription().equals("testIncome1")));
        assertFalse(incomes.stream().anyMatch(w -> w.getDescription().equals("testIncome2")));
        assertFalse(incomes.stream().anyMatch(w -> w.getDescription().equals("testIncome4")));
        assertEquals(1, incomes.stream().filter(w -> w.getDescription().equals("testIncome3")).count());
    }

    @ParameterizedTest
    @CsvSource({
            "testIncome1, 12.10",
            "testIncome2, 12030.60",
            "testIncome3, 20000.00",
    })
    void testUpdateIncome(String description, BigDecimal amount) {
        SaveIncomeDto incomeDto = new SaveIncomeDto(testCurrency.getId(),  BigDecimal.valueOf(1),"standard", LocalDate.now().plusDays(1));
        Income income = incomeService.saveIncome(incomeDto);

        UpdateIncomeDto updateIncomeDto = new UpdateIncomeDto(income.getId(), testCurrency.getId(), amount,description,  LocalDate.now().plusDays(1));

        Income updateIncome = incomeService.updateIncome(updateIncomeDto);
        assertEquals(income.getId(), updateIncome.getId());
        assertEquals(description, updateIncome.getDescription());
        assertEquals(amount, updateIncome.getAmount());
        assertEquals(testUser.getId(), updateIncome.getUser().getId());
        assertEquals(testCurrency.getId(), updateIncome.getCurrency().getId());

        Optional<Income> found = incomeRepo.findById(updateIncome.getId());
        assertTrue(found.isPresent());
        assertEquals(description, found.get().getDescription());
        assertEquals(amount, found.get().getAmount());
        assertEquals(updateIncomeDto.date(), found.get().getDate());
    }

    @ParameterizedTest
    @CsvSource({
            "testIncome1, 12.10",
            "testIncome2, 12030.60",
            "testIncome3, 20000.00",
    })
    void testDeleteIncome(String description, BigDecimal amount) {
        SaveIncomeDto saveIncomeDto = new SaveIncomeDto(testCurrency.getId(), amount,description,  LocalDate.now().plusDays(1));

        Income income = incomeService.saveIncome(saveIncomeDto);
        assertFalse(StreamSupport.stream(incomeRepo.findAll().spliterator(), false).toList().isEmpty());
        incomeService.deleteIncome(income.getId());
        assertTrue(StreamSupport.stream(incomeRepo.findAll().spliterator(), false).toList().isEmpty());
    }

}
