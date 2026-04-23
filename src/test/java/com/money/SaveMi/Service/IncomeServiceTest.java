package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Income.SaveIncomeDto;
import com.money.SaveMi.DTO.Income.UpdateIncomeDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Income;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.IncomeRepo;
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
public class IncomeServiceTest {

    @Mock
    private IncomeRepo incomeRepo;
    @Mock
    private AuthenticationServiceUtil authUtil;
    @Mock
    private UserRepo userRepo;
    @Mock
    private CurrencyRepo currencyRepo;

    @InjectMocks
    private IncomeService incomeService;

    private User testUser;
    private String userUuid = "test-uuid";
    private Currency testCurrency;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userUuid);
        testCurrency = new Currency();
        testCurrency.setId(1L);
    }

    @Test
    void testGetAllIncomeByUserIdNoMonth() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(incomeRepo.findByUserId(userUuid)).thenReturn(Arrays.asList(new Income(), new Income()));

        Iterable<Income> result = incomeService.getAllIncomeByUserId(Optional.empty());

        assertNotNull(result);
        verify(incomeRepo, times(1)).findByUserId(userUuid);
    }

    @Test
    void testGetAllIncomeByUserIdWithMonth() {
        YearMonth ym = YearMonth.of(2023, 10);
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(incomeRepo.findAllByUserIdAndYearMonth(userUuid, 2023, 10)).thenReturn(Arrays.asList(new Income()));

        Iterable<Income> result = incomeService.getAllIncomeByUserId(Optional.of(ym));

        assertNotNull(result);
        verify(incomeRepo, times(1)).findAllByUserIdAndYearMonth(userUuid, 2023, 10);
    }

    @Test
    void testGetIncomeByIdSuccess() {
        Income income = new Income();
        income.setId(1L);
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(incomeRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(income));

        Income result = incomeService.getIncomeById(1L);

        assertEquals(income, result);
    }

    @Test
    void testSaveIncomeSuccess() {
        SaveIncomeDto dto = new SaveIncomeDto(1L, new BigDecimal("1000"), "Salary", LocalDate.now());
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(userRepo.findById(userUuid)).thenReturn(Optional.of(testUser));
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testCurrency));
        when(incomeRepo.save(any(Income.class))).thenAnswer(i -> i.getArgument(0));

        Income result = incomeService.saveIncome(dto);

        assertNotNull(result);
        assertEquals(new BigDecimal("1000"), result.getAmount());
        assertEquals(testCurrency, result.getCurrency());
    }

    @Test
    void testUpdateIncomeSuccess() {
        UpdateIncomeDto dto = new UpdateIncomeDto(1L, 1L, new BigDecimal("1200"), "Bonus", LocalDate.now());
        Income oldIncome = new Income();
        oldIncome.setId(1L);

        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testCurrency));
        when(incomeRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(oldIncome));
        when(incomeRepo.save(any(Income.class))).thenAnswer(i -> i.getArgument(0));

        Income result = incomeService.updateIncome(dto);

        assertEquals(new BigDecimal("1200"), result.getAmount());
        assertEquals("Bonus", result.getDescription());
    }

    @Test
    void testBulkDeleteSuccess() {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(incomeRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Income()));
        when(incomeRepo.findByIdAndUserId(2L, userUuid)).thenReturn(Optional.of(new Income()));

        incomeService.bulkDelete(dto);

        verify(incomeRepo, times(1)).bulkDelete(dto.ids(), userUuid);
    }

    @Test
    void testDeleteIncomeSuccess() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(incomeRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Income()));

        incomeService.deleteIncome(1L);

        verify(incomeRepo, times(1)).deleteByIdAndUserId(1L, userUuid);
    }
}
