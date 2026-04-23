package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Currency.SaveCurrencyDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @Mock
    private CurrencyRepo currencyRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private AuthenticationServiceUtil authUtil;

    @InjectMocks
    private CurrencyService currencyService;

    private User testUser;
    private String userUuid = "test-uuid";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userUuid);
    }

    @Test
    void testGetAllCurrenciesFromUser() {
        Currency curr1 = new Currency(testUser, "Dollar", "USD");
        Currency curr2 = new Currency(testUser, "Euro", "EUR");
        
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(currencyRepo.findAllByUserId(userUuid)).thenReturn(Arrays.asList(curr1, curr2));

        Iterable<Currency> result = currencyService.getAllCurrenciesFromUser();

        assertNotNull(result);
        int count = 0;
        for (Currency ignored : result) count++;
        assertEquals(2, count);
    }

    @Test
    void testGetCurrencyByIdSuccess() {
        Currency curr = new Currency(testUser, "Dollar", "USD");
        curr.setId(1L);
        
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(curr));

        Currency result = currencyService.getCurrencyById(1L);

        assertEquals(curr, result);
    }

    @Test
    void testGetCurrencyByIdNotFound() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> currencyService.getCurrencyById(1L));
    }

    @Test
    void testSaveCurrencySuccess() {
        SaveCurrencyDto dto = new SaveCurrencyDto("Dollar", "USD");
        
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(userRepo.findById(userUuid)).thenReturn(Optional.of(testUser));
        when(currencyRepo.save(any(Currency.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Currency result = currencyService.saveCurrency(dto);

        assertNotNull(result);
        assertEquals("Dollar", result.getName());
        assertEquals("USD", result.getSymbol());
        assertEquals(testUser, result.getUser());
    }

    @Test
    void testSaveCurrencyUserNotFound() {
        SaveCurrencyDto dto = new SaveCurrencyDto("Dollar", "USD");
        
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(userRepo.findById(userUuid)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> currencyService.saveCurrency(dto));
    }

    @Test
    void testBulkDeleteSuccess() {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Currency()));
        when(currencyRepo.findByIdAndUserId(2L, userUuid)).thenReturn(Optional.of(new Currency()));

        currencyService.bulkDelete(dto);

        verify(currencyRepo, times(1)).bulkDelete(dto.ids(), userUuid);
    }

    @Test
    void testBulkDeleteNotFound() {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> currencyService.bulkDelete(dto));
    }

    @Test
    void testDeleteCurrencySuccess() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Currency()));

        currencyService.deleteCurrency(1L);

        verify(currencyRepo, times(1)).deleteByIdAndUserId(1L, userUuid);
    }

    @Test
    void testDeleteCurrencyNotFound() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> currencyService.deleteCurrency(1L));
    }

    @Test
    void testFindByNameAndSymbolAndUserId() {
        Currency curr = new Currency(testUser, "Dollar", "USD");
        
        when(currencyRepo.findByNameAndSymbolAndUserId("Dollar", "USD", userUuid)).thenReturn(Optional.of(curr));

        Currency result = currencyService.findByNameAndSymbolAndUserId("Dollar", "USD", userUuid);

        assertEquals(curr, result);
    }
}
