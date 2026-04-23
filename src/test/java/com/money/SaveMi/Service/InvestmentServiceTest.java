package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Investment.SaveInvestmentDto;
import com.money.SaveMi.DTO.Investment.UpdateInvestmentDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Investment;
import com.money.SaveMi.Model.StrategyType;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.InvestmentRepo;
import com.money.SaveMi.Repo.StrategyTypeRepo;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvestmentServiceTest {

    @Mock
    private InvestmentRepo investmentRepo;
    @Mock
    private AuthenticationServiceUtil authUtil;
    @Mock
    private CurrencyRepo currencyRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private StrategyTypeRepo strategyTypeRepo;

    @InjectMocks
    private InvestmentService investmentService;

    private User testUser;
    private String userUuid = "test-uuid";
    private Currency testCurrency;
    private StrategyType testStrategyType;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userUuid);
        testCurrency = new Currency();
        testCurrency.setId(1L);
        testStrategyType = new StrategyType();
        testStrategyType.setId(1L);
    }

    @Test
    void testGetAllInvestmentsNoMonth() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(investmentRepo.findAllByUserId(userUuid)).thenReturn(Arrays.asList(new Investment(), new Investment()));

        Iterable<Investment> result = investmentService.getAllInvestments(Optional.empty());

        assertNotNull(result);
        verify(investmentRepo, times(1)).findAllByUserId(userUuid);
    }

    @Test
    void testGetAllInvestmentsWithMonth() {
        YearMonth ym = YearMonth.of(2023, 10);
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(investmentRepo.findAllByUserIdAndYearMonth(userUuid, 2023, 10)).thenReturn(List.of(new Investment()));

        Iterable<Investment> result = investmentService.getAllInvestments(Optional.of(ym));

        assertNotNull(result);
        verify(investmentRepo, times(1)).findAllByUserIdAndYearMonth(userUuid, 2023, 10);
    }

    @Test
    void testGetInvestmentByIdSuccess() {
        Investment investment = new Investment();
        investment.setId(1L);
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(investmentRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(investment));

        Investment result = investmentService.getInvestmentById(1L);

        assertEquals(investment, result);
    }

    @Test
    void testSaveInvestmentSuccess() {
        SaveInvestmentDto dto = new SaveInvestmentDto(1L, 1L, new BigDecimal("5000"), "Stock Purchase", LocalDate.now());
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(userRepo.findById(userUuid)).thenReturn(Optional.of(testUser));
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testCurrency));
        when(strategyTypeRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testStrategyType));
        when(investmentRepo.save(any(Investment.class))).thenAnswer(i -> i.getArgument(0));

        Investment result = investmentService.saveInvestment(dto);

        assertNotNull(result);
        assertEquals(new BigDecimal("5000"), result.getAmount());
        assertEquals(testCurrency, result.getCurrency());
        assertEquals(testStrategyType, result.getStrategyType());
    }

    @Test
    void testUpdateInvestmentSuccess() {
        UpdateInvestmentDto dto = new UpdateInvestmentDto(1L, 1L, 1L, new BigDecimal("6000"), "More Stocks", LocalDate.now());
        Investment oldInvestment = new Investment();
        oldInvestment.setId(1L);

        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testCurrency));
        when(strategyTypeRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testStrategyType));
        when(investmentRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(oldInvestment));
        when(investmentRepo.save(any(Investment.class))).thenAnswer(i -> i.getArgument(0));

        Investment result = investmentService.updateInvestment(dto);

        assertEquals(new BigDecimal("6000"), result.getAmount());
        assertEquals("More Stocks", result.getDescription());
    }

    @Test
    void testBulkDeleteSuccess() {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(investmentRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Investment()));
        when(investmentRepo.findByIdAndUserId(2L, userUuid)).thenReturn(Optional.of(new Investment()));

        investmentService.bulkDelete(dto);

        verify(investmentRepo, times(1)).bulkDelete(dto.ids(), userUuid);
    }

    @Test
    void testDeleteInvestmentSuccess() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(investmentRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Investment()));

        investmentService.deleteInvestment(1L);

        verify(investmentRepo, times(1)).deleteByIdAndUserId(1L, userUuid);
    }
}
