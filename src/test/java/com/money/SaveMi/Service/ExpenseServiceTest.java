package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Expense.SaveExpenseDto;
import com.money.SaveMi.DTO.Expense.UpdateExpenseDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Category;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Expense;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CategoryRepo;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.ExpenseRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepo expenseRepo;
    @Mock
    private AuthenticationServiceUtil authUtil;
    @Mock
    private CurrencyRepo currencyRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private CategoryRepo categoryRepo;

    @InjectMocks
    private ExpenseService expenseService;

    private User testUser;
    private String userUuid = "test-uuid";
    private Currency testCurrency;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userUuid);
        testCurrency = new Currency();
        testCurrency.setId(1L);
        testCategory = new Category();
        testCategory.setId(1L);
    }

    @Test
    void testGetAllExpensesNoMonth() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(expenseRepo.findAllByUserId(userUuid)).thenReturn(Arrays.asList(new Expense(), new Expense()));

        Iterable<Expense> result = expenseService.getAllExpenses(Optional.empty());

        assertNotNull(result);
        verify(expenseRepo, times(1)).findAllByUserId(userUuid);
    }

    @Test
    void testGetAllExpensesWithMonth() {
        YearMonth ym = YearMonth.of(2023, 10);
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(expenseRepo.findAllByUserIdAndYearMonth(userUuid, 2023, 10)).thenReturn(Arrays.asList(new Expense()));

        Iterable<Expense> result = expenseService.getAllExpenses(Optional.of(ym));

        assertNotNull(result);
        verify(expenseRepo, times(1)).findAllByUserIdAndYearMonth(userUuid, 2023, 10);
    }

    @Test
    void testGetExpenseByIdSuccess() {
        Expense expense = new Expense();
        expense.setId(1L);
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(expenseRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(expense));

        Expense result = expenseService.getExpenseById(1L);

        assertEquals(expense, result);
    }

    @Test
    void testSaveExpenseSuccess() {
        SaveExpenseDto dto = new SaveExpenseDto(1L, 1L, new BigDecimal("100"), "Desc", LocalDate.now());
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(userRepo.findById(userUuid)).thenReturn(Optional.of(testUser));
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testCurrency));
        when(categoryRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testCategory));
        when(expenseRepo.save(any(Expense.class))).thenAnswer(i -> i.getArgument(0));

        Expense result = expenseService.saveExpense(dto);

        assertNotNull(result);
        assertEquals(new BigDecimal("100"), result.getAmount());
        assertEquals(testCurrency, result.getCurrency());
        assertEquals(testCategory, result.getCategory());
    }

    @Test
    void testUpdateExpenseSuccess() {
        UpdateExpenseDto dto = new UpdateExpenseDto(1L, 1L, 1L, new BigDecimal("200"), "New Desc", LocalDate.now());
        Expense oldExpense = new Expense();
        oldExpense.setId(1L);

        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testCurrency));
        when(categoryRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testCategory));
        when(expenseRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(oldExpense));
        when(expenseRepo.save(any(Expense.class))).thenAnswer(i -> i.getArgument(0));

        Expense result = expenseService.updateExpense(dto);

        assertEquals(new BigDecimal("200"), result.getAmount());
        assertEquals("New Desc", result.getDescription());
    }

    @Test
    void testBulkDeleteSuccess() {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(expenseRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Expense()));
        when(expenseRepo.findByIdAndUserId(2L, userUuid)).thenReturn(Optional.of(new Expense()));

        expenseService.bulkDelete(dto);

        verify(expenseRepo, times(1)).bulkDelete(dto.ids(), userUuid);
    }

    @Test
    void testDeleteExpenseSuccess() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(expenseRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Expense()));

        expenseService.deleteExpense(1L);

        verify(expenseRepo, times(1)).deleteByIdAndUserId(1L, userUuid);
    }
}
