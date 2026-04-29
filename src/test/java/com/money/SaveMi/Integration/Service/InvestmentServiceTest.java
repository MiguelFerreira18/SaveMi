package com.money.SaveMi.Integration.Service;

import com.money.SaveMi.DTO.Expense.SaveExpenseDto;
import com.money.SaveMi.DTO.Expense.UpdateExpenseDto;
import com.money.SaveMi.DTO.Investment.SaveInvestmentDto;
import com.money.SaveMi.DTO.Investment.UpdateInvestmentDto;
import com.money.SaveMi.Integration.BaseTest;
import com.money.SaveMi.Model.*;
import com.money.SaveMi.Repo.*;
import com.money.SaveMi.Service.InvestmentService;
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

public class InvestmentServiceTest extends BaseTest {
    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private InvestmentRepo investmentRepo;

    @Autowired
    private CurrencyRepo currencyRepo;
    @Autowired
    private StrategyTypeRepo strategyTypeRepo;
    @Autowired
    private UserRepo userRepo;

    private Currency testCurrency;
    private StrategyType testStrategyType;
    private User testUser;

    @BeforeEach
    void setUp() {
        investmentRepo.deleteAll();
        currencyRepo.deleteAll();
        userRepo.deleteAll();

        testUser = new User("test@example.com", "testuser", "Password123!");
        testUser = userRepo.save(testUser);

        testCurrency = new Currency(testUser, "euro", "EUR");
        testCurrency = currencyRepo.save(testCurrency);

        testStrategyType = new StrategyType(testUser, "ETF", "Investments based on ETFs");
        testStrategyType = strategyTypeRepo.save(testStrategyType);

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
            "1000.50, ETF",
            "10000.13, Certificates",
            "100.99, High Risk",
    })
    void testSaveInvestment(BigDecimal amount, String description) {
        SaveInvestmentDto saveInvestmentDto = new SaveInvestmentDto(testCurrency.getId(), testStrategyType.getId(), amount, description, LocalDate.now().plusDays(1));

        Investment investment = investmentService.saveInvestment(saveInvestmentDto);
        assertNotNull(investment.getId());
        assertEquals(description, investment.getDescription());
        assertEquals(amount, investment.getAmount());
        assertEquals(testCurrency.getId(), investment.getCurrency().getId());
        assertEquals(testStrategyType.getId(), investment.getStrategyType().getId());
        assertEquals(testUser.getId(), investment.getUser().getId());

        Optional<Investment> found = investmentRepo.findById(investment.getId());

        assertTrue(found.isPresent());
        assertEquals(description, found.get().getDescription());
        assertEquals(amount, found.get().getAmount());
        assertEquals(testStrategyType.getId(), found.get().getStrategyType().getId());
        assertEquals(testCurrency.getId(), found.get().getCurrency().getId());
    }

    @Test
    void testGetAllInvestmentsWithoutFilter() {
        SaveInvestmentDto firstSaveExpenseDto = new SaveInvestmentDto(testCurrency.getId(), testStrategyType.getId(), BigDecimal.valueOf(12.1), "testInvestment1", LocalDate.now().plusDays(1));
        SaveInvestmentDto secondSaveExpenseDto = new SaveInvestmentDto(testCurrency.getId(), testStrategyType.getId(), BigDecimal.valueOf(133000.1), "testInvestment2", LocalDate.now().plusDays(1));
        SaveInvestmentDto thirdSaveExpenseDto = new SaveInvestmentDto(testCurrency.getId(), testStrategyType.getId(), BigDecimal.valueOf(2000), "testInvestment3", LocalDate.now().plusDays(1));

        investmentService.saveInvestment(firstSaveExpenseDto);
        investmentService.saveInvestment(secondSaveExpenseDto);
        investmentService.saveInvestment(thirdSaveExpenseDto);

        List<Investment> investments = StreamSupport.stream(investmentService.getAllInvestments(Optional.empty()).spliterator(), false).toList();

        assertEquals(3, investments.size());
        assertTrue(investments.stream().anyMatch(w -> w.getDescription().equals("testInvestment1")));
        assertTrue(investments.stream().anyMatch(w -> w.getDescription().equals("testInvestment2")));
        assertTrue(investments.stream().anyMatch(w -> w.getDescription().equals("testInvestment3")));
        assertFalse(investments.stream().anyMatch(w -> w.getDescription().equals("testInvestment4")));
        assertEquals(1, investments.stream().filter(w -> w.getDescription().equals("testInvestment1")).count());
        assertEquals(1, investments.stream().filter(w -> w.getDescription().equals("testInvestment2")).count());
        assertEquals(1, investments.stream().filter(w -> w.getDescription().equals("testInvestment3")).count());
    }

    @ParameterizedTest
    @CsvSource({
            "2025-01",
            "2026-06",
            "2026-12",
    })
    void testGetAllInvestmentsWithFilter(YearMonth month) {
        SaveInvestmentDto firstSaveExpenseDto = new SaveInvestmentDto(testCurrency.getId(), testStrategyType.getId(), BigDecimal.valueOf(12.1), "testInvestment1", month.atDay(1).plusMonths(1));
        SaveInvestmentDto secondSaveExpenseDto = new SaveInvestmentDto(testCurrency.getId(), testStrategyType.getId(), BigDecimal.valueOf(133000.1), "testInvestment2", month.atDay(1).minusMonths(1));
        SaveInvestmentDto thirdSaveExpenseDto = new SaveInvestmentDto(testCurrency.getId(), testStrategyType.getId(), BigDecimal.valueOf(2000), "testInvestment3", month.atDay(1));

        investmentService.saveInvestment(firstSaveExpenseDto);
        investmentService.saveInvestment(secondSaveExpenseDto);
        investmentService.saveInvestment(thirdSaveExpenseDto);

        List<Investment> investments = StreamSupport.stream(investmentService.getAllInvestments(Optional.of(month)).spliterator(), false).toList();

        assertEquals(1, investments.size());
        assertTrue(investments.stream().anyMatch(w -> w.getDescription().equals("testInvestment3")));
        assertFalse(investments.stream().anyMatch(w -> w.getDescription().equals("testInvestment1")));
        assertFalse(investments.stream().anyMatch(w -> w.getDescription().equals("testInvestment2")));
        assertFalse(investments.stream().anyMatch(w -> w.getDescription().equals("testInvestment4")));
        assertEquals(1, investments.stream().filter(w -> w.getDescription().equals("testInvestment3")).count());
    }

    @ParameterizedTest
    @CsvSource({
            "1000.50, ETF",
            "10000.13, Certificates",
            "100.99, High Risk",
    })
    void testUpdateInvestment(BigDecimal amount, String description) {
        SaveInvestmentDto investmentDto = new SaveInvestmentDto(testCurrency.getId(), testStrategyType.getId(), BigDecimal.valueOf(1), "standard", LocalDate.now().plusDays(1));
        Investment investment = investmentService.saveInvestment(investmentDto);

        UpdateInvestmentDto updateInvestmentDto = new UpdateInvestmentDto(investment.getId(), testCurrency.getId(), testStrategyType.getId(), amount, description, LocalDate.now().plusDays(1));

        Investment updateInvestment = investmentService.updateInvestment(updateInvestmentDto);
        assertEquals(investment.getId(), updateInvestment.getId());
        assertEquals(description, updateInvestment.getDescription());
        assertEquals(amount, updateInvestment.getAmount());
        assertEquals(testUser.getId(), updateInvestment.getUser().getId());
        assertEquals(testCurrency.getId(), updateInvestment.getCurrency().getId());
        assertEquals(testStrategyType.getId(), updateInvestment.getStrategyType().getId());

        Optional<Investment> found = investmentRepo.findById(updateInvestment.getId());
        assertTrue(found.isPresent());
        assertEquals(description, found.get().getDescription());
        assertEquals(amount, found.get().getAmount());
        assertEquals(updateInvestmentDto.date(), found.get().getDate());

    }

    @ParameterizedTest
    @CsvSource({
            "1000.50, ETF",
            "10000.13, Certificates",
            "100.99, High Risk",
    })
    void testDeleteInvestment(BigDecimal amount, String description) {
        SaveInvestmentDto investmentDto = new SaveInvestmentDto(testCurrency.getId(), testStrategyType.getId(), amount, description, LocalDate.now().plusDays(1));

        Investment investment = investmentService.saveInvestment(investmentDto);
        assertFalse(StreamSupport.stream(investmentRepo.findAll().spliterator(), false).toList().isEmpty());
        investmentService.deleteInvestment(investment.getId());
        assertTrue(StreamSupport.stream(investmentRepo.findAll().spliterator(), false).toList().isEmpty());
    }
}
