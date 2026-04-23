package com.money.SaveMi.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.money.SaveMi.DTO.Currency.SaveCurrencyDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CurrencyController currencyController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Currency currency;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(currencyController).build();
        User testUser = new User();
        testUser.setId("user-id");
        currency = new Currency(testUser, "Euro", "EUR");
        currency.setId(1L);
    }

    @Test
    void testGetAllCurrencies() throws Exception {
        when(currencyService.getAllCurrenciesFromUser()).thenReturn(Collections.singletonList(currency));

        mockMvc.perform(get("/api/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Euro"));
    }

    @Test
    void testGetCurrencyById() throws Exception {
        when(currencyService.getCurrencyById(1L)).thenReturn(currency);

        mockMvc.perform(get("/api/currencies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.symbol").value("EUR"));
    }

    @Test
    void testSaveCurrency() throws Exception {
        SaveCurrencyDto dto = new SaveCurrencyDto("Euro", "EUR");
        when(currencyService.saveCurrency(any(SaveCurrencyDto.class))).thenReturn(currency);

        mockMvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Euro"));
    }

    @Test
    void testDeleteCurrenciesBulk() throws Exception {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        doNothing().when(currencyService).bulkDelete(any(BulkDeleteDto.class));

        mockMvc.perform(delete("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCurrencySingle() throws Exception {
        doNothing().when(currencyService).deleteCurrency(1L);

        mockMvc.perform(delete("/api/currencies/1"))
                .andExpect(status().isAccepted());
    }
}
