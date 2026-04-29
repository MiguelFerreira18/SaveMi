package com.money.SaveMi.Integration.Service;

import com.money.SaveMi.DTO.Expense.SaveExpenseDto;
import com.money.SaveMi.DTO.Expense.UpdateExpenseDto;
import com.money.SaveMi.DTO.Wish.SaveWishDto;
import com.money.SaveMi.DTO.Wish.UpdateWishDto;
import com.money.SaveMi.Integration.BaseTest;
import com.money.SaveMi.Model.*;
import com.money.SaveMi.Repo.CategoryRepo;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.ExpenseRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Service.CategoryService;
import com.money.SaveMi.Service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseServiceTest extends BaseTest {
    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseRepo expenseRepo;

    @Autowired
    private CurrencyRepo currencyRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private UserRepo userRepo;

    private Currency testCurrency;
    private Category testCategory;
    private User testUser;

    @BeforeEach
    void setUp() {
        expenseRepo.deleteAll();
        currencyRepo.deleteAll();
        userRepo.deleteAll();

        testUser = new User("test@example.com", "testuser", "Password123!");
        testUser = userRepo.save(testUser);

        testCurrency = new Currency(testUser, "euro", "EUR");
        testCurrency = currencyRepo.save(testCurrency);

        testCategory = new Category(testUser, "testCategory", "Test Category");
        testCategory = categoryRepo.save(testCategory);

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
            "1000.50, monthly rent",
            "10000.13, New Car",
            "100.99, New Steam Controller",
    })
    void testSaveExpense(BigDecimal amount, String description) {
        SaveExpenseDto expenseDto = new SaveExpenseDto(testCurrency.getId(), testCategory.getId(), amount, description, LocalDate.now().plusDays(1));

        Expense expense = expenseService.saveExpense(expenseDto);
        assertNotNull(expense.getId());
        assertEquals(description, expense.getDescription());
        assertEquals(amount, expense.getAmount());
        assertEquals(testCurrency.getId(), expense.getCurrency().getId());
        assertEquals(testCategory.getId(), expense.getCategory().getId());
        assertEquals(testUser.getId(), expense.getUser().getId());

        Optional<Expense> found = expenseRepo.findById(expense.getId());

        assertTrue(found.isPresent());
        assertEquals(description, found.get().getDescription());
        assertEquals(amount, found.get().getAmount());
        assertEquals(testCategory.getId(), found.get().getCategory().getId());
        assertEquals(testCurrency.getId(), found.get().getCurrency().getId());
    }

    @Test
    void testGetAllExpenseWithoutFilter() {
        SaveExpenseDto firstSaveExpenseDto = new SaveExpenseDto(testCurrency.getId(), testCategory.getId(), BigDecimal.valueOf(12.1), "testExpense1", LocalDate.now().plusDays(1));
        SaveExpenseDto secondSaveExpenseDto = new SaveExpenseDto(testCurrency.getId(), testCategory.getId(), BigDecimal.valueOf(133000.1), "testExpense2", LocalDate.now().plusDays(1));
        SaveExpenseDto thirdSaveExpenseDto = new SaveExpenseDto(testCurrency.getId(), testCategory.getId(), BigDecimal.valueOf(2000), "testExpense3", LocalDate.now().plusDays(1));

        expenseService.saveExpense(firstSaveExpenseDto);
        expenseService.saveExpense(secondSaveExpenseDto);
        expenseService.saveExpense(thirdSaveExpenseDto);

        List<Expense> expenses = StreamSupport.stream(expenseService.getAllExpenses(Optional.empty()).spliterator(), false).toList();

        assertEquals(3, expenses.size());
        assertTrue(expenses.stream().anyMatch(w -> w.getDescription().equals("testExpense1")));
        assertTrue(expenses.stream().anyMatch(w -> w.getDescription().equals("testExpense2")));
        assertTrue(expenses.stream().anyMatch(w -> w.getDescription().equals("testExpense3")));
        assertFalse(expenses.stream().anyMatch(w -> w.getDescription().equals("testExpense4")));
        assertEquals(1, expenses.stream().filter(w -> w.getDescription().equals("testExpense1")).count());
        assertEquals(1, expenses.stream().filter(w -> w.getDescription().equals("testExpense2")).count());
        assertEquals(1, expenses.stream().filter(w -> w.getDescription().equals("testExpense3")).count());
    }

    @ParameterizedTest
    @CsvSource({
            "2025-01",
            "2026-06",
            "2026-12",
    })
    void testGetAllExpenseWithFilter(YearMonth month) {
        SaveExpenseDto firstSaveExpenseDto = new SaveExpenseDto(testCurrency.getId(), testCategory.getId(), BigDecimal.valueOf(12.1), "testExpense1", month.atDay(1).plusMonths(1));
        SaveExpenseDto secondSaveExpenseDto = new SaveExpenseDto(testCurrency.getId(), testCategory.getId(), BigDecimal.valueOf(133000.1), "testExpense2", month.atDay(1).minusMonths(1));
        SaveExpenseDto thirdSaveExpenseDto = new SaveExpenseDto(testCurrency.getId(), testCategory.getId(), BigDecimal.valueOf(2000), "testExpense3", month.atDay(1));

        expenseService.saveExpense(firstSaveExpenseDto);
        expenseService.saveExpense(secondSaveExpenseDto);
        expenseService.saveExpense(thirdSaveExpenseDto);

        List<Expense> expenses = StreamSupport.stream(expenseService.getAllExpenses(Optional.of(month)).spliterator(), false).toList();

        assertEquals(1, expenses.size());
        assertTrue(expenses.stream().anyMatch(w -> w.getDescription().equals("testExpense3")));
        assertFalse(expenses.stream().anyMatch(w -> w.getDescription().equals("testExpense1")));
        assertFalse(expenses.stream().anyMatch(w -> w.getDescription().equals("testExpense2")));
        assertFalse(expenses.stream().anyMatch(w -> w.getDescription().equals("testExpense4")));
        assertEquals(1, expenses.stream().filter(w -> w.getDescription().equals("testExpense3")).count());
    }

    @ParameterizedTest
    @CsvSource({
            "1000.50, monthly rent",
            "10000.13, New Car",
            "100.99, New Steam Controller",
    })
    void testUpdateExpense(BigDecimal amount, String description) {
        SaveExpenseDto expenseDto = new SaveExpenseDto(testCurrency.getId(), testCategory.getId(), BigDecimal.valueOf(1), "standard", LocalDate.now().plusDays(1));
        Expense expense = expenseService.saveExpense(expenseDto);

        UpdateExpenseDto updateExpenseDto = new UpdateExpenseDto(expense.getId(), testCurrency.getId(), testCategory.getId(), amount, description, LocalDate.now().plusDays(1));

        Expense updateExpense = expenseService.updateExpense(updateExpenseDto);
        assertEquals(expense.getId(), updateExpense.getId());
        assertEquals(description, updateExpense.getDescription());
        assertEquals(amount, updateExpense.getAmount());
        assertEquals(testUser.getId(), updateExpense.getUser().getId());
        assertEquals(testCurrency.getId(), updateExpense.getCurrency().getId());
        assertEquals(testCategory.getId(), updateExpense.getCategory().getId());

        Optional<Expense> found = expenseRepo.findById(updateExpense.getId());
        assertTrue(found.isPresent());
        assertEquals(description, found.get().getDescription());
        assertEquals(amount, found.get().getAmount());
        assertEquals(updateExpenseDto.date(), found.get().getDate());

    }

    @ParameterizedTest
    @CsvSource({
            "1000.50, monthly rent",
            "10000.13, New Car",
            "100.99, New Steam Controller",
    })
    void testDeleteExpense(BigDecimal amount, String description) {
        SaveExpenseDto expenseDto = new SaveExpenseDto(testCurrency.getId(),testCategory.getId(), amount, description , LocalDate.now().plusDays(1));

        Expense expense = expenseService.saveExpense(expenseDto);
        assertFalse(StreamSupport.stream(expenseRepo.findAll().spliterator(), false).toList().isEmpty());
        expenseService.deleteExpense(expense.getId());
        assertTrue(StreamSupport.stream(expenseRepo.findAll().spliterator(), false).toList().isEmpty());
    }

}
