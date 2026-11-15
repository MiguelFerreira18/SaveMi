package com.money.SaveMi.Integration.Service;

import com.money.SaveMi.DTO.Wish.SaveWishDto;
import com.money.SaveMi.DTO.Wish.UpdateWishDto;
import com.money.SaveMi.Integration.BaseTest;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Model.Wish;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Repo.WishRepo;
import com.money.SaveMi.Service.WishService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class WishServiceTest extends BaseTest {

    @Autowired
    private WishService wishService;

    @Autowired
    private WishRepo wishRepo;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CurrencyRepo currencyRepo;

    private Currency testCurrency;
    private User testUser;

    @BeforeEach
    void setUp() {
        wishRepo.deleteAll();
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
    void testSaveWish(String description, BigDecimal amount) {
        SaveWishDto wishDto = new SaveWishDto(testCurrency.getId(), description, amount, LocalDate.now().plusDays(1));

        Wish wish = wishService.saveWish(wishDto);

        assertNotNull(wish.getId());
        assertEquals(description, wish.getDescription());
        assertEquals(amount, wish.getAmount());
        assertEquals(testCurrency.getId(), wish.getCurrency().getId());
        assertEquals(testUser.getId(), wish.getUser().getId());

        Optional<Wish> found = wishRepo.findById(wish.getId());

        assertTrue(found.isPresent());
        assertEquals(description, found.get().getDescription());
        assertEquals(amount, found.get().getAmount());
        assertEquals(testCurrency.getId(), wish.getCurrency().getId());
    }

    @Test
    void testGetAllWishes() {
        SaveWishDto firstSaveWishDto = new SaveWishDto(testCurrency.getId(), "testWish1", BigDecimal.valueOf(12.1), LocalDate.now().plusDays(1));
        SaveWishDto secondSaveWishDto = new SaveWishDto(testCurrency.getId(), "testWish2", BigDecimal.valueOf(133000.1), LocalDate.now().plusDays(1));
        SaveWishDto thirdSaveWishDto = new SaveWishDto(testCurrency.getId(), "testWish3", BigDecimal.valueOf(2000), LocalDate.now().plusDays(1));

        wishService.saveWish(firstSaveWishDto);
        wishService.saveWish(secondSaveWishDto);
        wishService.saveWish(thirdSaveWishDto);

        List<Wish> wishes = StreamSupport.stream(wishService.getAllWishes(Optional.empty()).spliterator(), false).toList();

        assertEquals(3, wishes.size());
        assertTrue(wishes.stream().anyMatch(w -> w.getDescription().equals("testWish1")));
        assertTrue(wishes.stream().anyMatch(w -> w.getDescription().equals("testWish2")));
        assertTrue(wishes.stream().anyMatch(w -> w.getDescription().equals("testWish3")));
        assertFalse(wishes.stream().anyMatch(w -> w.getDescription().equals("testWish4")));
        assertEquals(1, wishes.stream().filter(w -> w.getDescription().equals("testWish1")).count());
        assertEquals(1, wishes.stream().filter(w -> w.getDescription().equals("testWish2")).count());
        assertEquals(1, wishes.stream().filter(w -> w.getDescription().equals("testWish3")).count());
    }

    @ParameterizedTest
    @CsvSource({
            "testWish1, 12.10",
            "testWish2, 12030.60",
            "testWish3, 20000.00",
    })
    void testUpdateWish(String description, BigDecimal amount) {
        SaveWishDto wishDto = new SaveWishDto(testCurrency.getId(), "standard", BigDecimal.valueOf(1), LocalDate.now().plusDays(1));
        Wish wish = wishService.saveWish(wishDto);

        UpdateWishDto updateWishDto = new UpdateWishDto(wish.getId(), testCurrency.getId(), description, amount, LocalDate.now().plusDays(1));

        Wish updatedWish = wishService.updateWish(updateWishDto);
        assertEquals(wish.getId(), updatedWish.getId());
        assertEquals(description, updatedWish.getDescription());
        assertEquals(amount, updatedWish.getAmount());
        assertEquals(testUser.getId(), updatedWish.getUser().getId());
        assertEquals(testCurrency.getId(), updatedWish.getCurrency().getId());

        Optional<Wish> found = wishRepo.findById(updatedWish.getId());
        assertTrue(found.isPresent());
        assertEquals(description, found.get().getDescription());
        assertEquals(amount, found.get().getAmount());
        assertEquals(updateWishDto.date(), found.get().getDate());
    }

    @ParameterizedTest
    @CsvSource({
            "testWish1, 12.10",
            "testWish2, 12030.60",
            "testWish3, 20000.00",
    })
    void testDeleteWish(String description, BigDecimal amount) {
        SaveWishDto wishDto = new SaveWishDto(testCurrency.getId(), description, amount, LocalDate.now().plusDays(1));

        Wish wish = wishService.saveWish(wishDto);
        assertFalse(StreamSupport.stream(wishRepo.findAll().spliterator(), false).toList().isEmpty());
        wishService.deleteWishById(wish.getId());
        assertTrue(StreamSupport.stream(wishRepo.findAll().spliterator(), false).toList().isEmpty());
    }
}
