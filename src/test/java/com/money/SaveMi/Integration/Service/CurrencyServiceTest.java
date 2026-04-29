package com.money.SaveMi.Integration.Service;

import com.money.SaveMi.DTO.Currency.SaveCurrencyDto;
import com.money.SaveMi.Integration.BaseTest;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class CurrencyServiceTest extends BaseTest {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CurrencyRepo currencyRepo;

    @Autowired
    private UserRepo userRepo;

    private User testUser;

    @BeforeEach
    void setUp() {
        currencyRepo.deleteAll();
        userRepo.deleteAll();

        testUser = new User("test@example.com", "testuser", "Password123!");
        testUser = userRepo.save(testUser);

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
            "US Dollar, USD",
            "Euro, EUR",
            "British Pound, GBP",
            "Japanese Yen, JPY"
    })
    void testSaveCurrency(String name, String symbol) {
        SaveCurrencyDto dto = new SaveCurrencyDto(name, symbol);

        Currency savedCurrency = currencyService.saveCurrency(dto);

        assertNotNull(savedCurrency.getId());
        assertEquals(name, savedCurrency.getName());
        assertEquals(symbol, savedCurrency.getSymbol());
        assertEquals(testUser.getId(), savedCurrency.getUser().getId());

        Optional<Currency> found = currencyRepo.findById(savedCurrency.getId());
        assertTrue(found.isPresent());
        assertEquals(name, found.get().getName());
        assertEquals(symbol, found.get().getSymbol());
    }

    @Test
    void testGetAllCurrencies() {
        SaveCurrencyDto firstDto = new SaveCurrencyDto("US Dollar", "USD");
        SaveCurrencyDto secondDto = new SaveCurrencyDto("Euro", "EUR");
        SaveCurrencyDto thirdDto = new SaveCurrencyDto("British Pound", "GBP");

        currencyService.saveCurrency(firstDto);
        currencyService.saveCurrency(secondDto);
        currencyService.saveCurrency(thirdDto);

        List<Currency> currenciesList = StreamSupport.stream(currencyService.getAllCurrenciesFromUser().spliterator(), false).toList();

        assertEquals(3, currenciesList.size());
        assertTrue(currenciesList.stream().anyMatch(c -> c.getSymbol().equals("USD")));
        assertTrue(currenciesList.stream().anyMatch(c -> c.getSymbol().equals("EUR")));
        assertTrue(currenciesList.stream().anyMatch(c -> c.getSymbol().equals("GBP")));
        assertEquals(1, currenciesList.stream().filter(c -> c.getSymbol().equals("USD")).count());
        assertEquals(1, currenciesList.stream().filter(c -> c.getSymbol().equals("EUR")).count());
        assertEquals(1, currenciesList.stream().filter(c -> c.getSymbol().equals("GBP")).count());

    }

    @ParameterizedTest
    @CsvSource({
            "US Dollar, USD",
            "Euro, EUR",
            "British Pound, GBP",
            "Japanese Yen, JPY"
    })
    void testDeleteCurrency(String name, String symbol) {
        SaveCurrencyDto dto = new SaveCurrencyDto(name, symbol);

        Currency savedCurrency = currencyService.saveCurrency(dto);

        assertFalse(StreamSupport.stream(currencyRepo.findAll().spliterator(), false).toList().isEmpty());
        currencyService.deleteCurrency(savedCurrency.getId());
        assertTrue(StreamSupport.stream(currencyRepo.findAll().spliterator(), false).toList().isEmpty());


    }

}
