package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.DTO.Wish.SaveWishDto;
import com.money.SaveMi.DTO.Wish.UpdateWishDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Model.Wish;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Repo.WishRepo;
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
public class WishServiceTest {

    @Mock
    private WishRepo wishRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private CurrencyRepo currencyRepo;
    @Mock
    private AuthenticationServiceUtil authUtil;

    @InjectMocks
    private WishService wishService;

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
    void testGetAllWishesNoMonth() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(wishRepo.findAllByUserId(userUuid)).thenReturn(Arrays.asList(new Wish(), new Wish()));

        Iterable<Wish> result = wishService.getAllWishes(Optional.empty());

        assertNotNull(result);
        verify(wishRepo, times(1)).findAllByUserId(userUuid);
    }

    @Test
    void testGetAllWishesWithMonth() {
        YearMonth ym = YearMonth.of(2023, 10);
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(wishRepo.findAllByUserIdFilteredByMonthAndYear(userUuid, 2023, 10)).thenReturn(Arrays.asList(new Wish()));

        Iterable<Wish> result = wishService.getAllWishes(Optional.of(ym));

        assertNotNull(result);
        verify(wishRepo, times(1)).findAllByUserIdFilteredByMonthAndYear(userUuid, 2023, 10);
    }

    @Test
    void testGetWishByIdSuccess() {
        Wish wish = new Wish();
        wish.setId(1L);
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(wishRepo.findByUserIdAndCurrencyId(userUuid, 1L)).thenReturn(Optional.of(wish));

        Wish result = wishService.getWishById(1L);

        assertEquals(wish, result);
    }

    @Test
    void testSaveWishSuccess() {
        SaveWishDto dto = new SaveWishDto(1L, "New Bike", new BigDecimal("500"), LocalDate.now());
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(userRepo.findById(userUuid)).thenReturn(Optional.of(testUser));
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testCurrency));
        when(wishRepo.save(any(Wish.class))).thenAnswer(i -> i.getArgument(0));

        Wish result = wishService.saveWish(dto);

        assertNotNull(result);
        assertEquals(new BigDecimal("500"), result.getAmount());
    }

    @Test
    void testUpdateWishSuccess() {
        UpdateWishDto dto = new UpdateWishDto(1L, 1L, "Expensive Bike", new BigDecimal("600"), LocalDate.now());
        Wish oldWish = new Wish();
        oldWish.setId(1L);

        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(wishRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(oldWish));
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testCurrency));
        when(wishRepo.save(any(Wish.class))).thenAnswer(i -> i.getArgument(0));

        Wish result = wishService.updateWish(dto);

        assertEquals(new BigDecimal("600"), result.getAmount());
        assertEquals("Expensive Bike", result.getDescription());
    }

    @Test
    void testBulkDeleteSuccess() {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(wishRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Wish()));
        when(wishRepo.findByIdAndUserId(2L, userUuid)).thenReturn(Optional.of(new Wish()));

        wishService.bulkDelete(dto);

        verify(wishRepo, times(1)).bulkDelete(dto.ids(), userUuid);
    }

    @Test
    void testDeleteWishByIdSuccess() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(wishRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Wish()));

        wishService.deleteWishById(1L);

        verify(wishRepo, times(1)).deleteByIdAndUserId(1L, userUuid);
    }
}
