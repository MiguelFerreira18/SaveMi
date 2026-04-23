package com.money.SaveMi.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.money.SaveMi.DTO.Income.SaveIncomeDto;
import com.money.SaveMi.DTO.Income.UpdateIncomeDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Income;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Service.IncomeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class IncomeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IncomeService incomeService;

    @InjectMocks
    private IncomeController incomeController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Income income;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(incomeController).build();

        User testUser = new User();
        testUser.setId("user-id");

        Currency currency = new Currency();
        currency.setSymbol("USD");
        
        income = new Income(testUser, currency, new BigDecimal("1000.00"), "Salary", LocalDate.now());
        income.setId(1L);
    }

    @Test
    void testGetAllIncomes() throws Exception {
        when(incomeService.getAllIncomeByUserId(any(Optional.class))).thenReturn(Collections.singletonList(income));

        mockMvc.perform(get("/api/incomes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Salary"));
    }

    @Test
    void testGetIncomeById() throws Exception {
        when(incomeService.getIncomeById(anyLong())).thenReturn(income);

        mockMvc.perform(get("/api/incomes/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveIncome() throws Exception {
        SaveIncomeDto dto = new SaveIncomeDto(1L, new BigDecimal("1000.00"), "Salary", LocalDate.now());
        when(incomeService.saveIncome(any(SaveIncomeDto.class))).thenReturn(income);

        mockMvc.perform(post("/api/incomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Salary"));
    }

    @Test
    void testUpdateIncome() throws Exception {
        UpdateIncomeDto dto = new UpdateIncomeDto(1L, 1L, new BigDecimal("1100.00"), "Bonus", LocalDate.now());
        income.setAmount(new BigDecimal("1100.00"));
        income.setDescription("Bonus");
        when(incomeService.updateIncome(any(UpdateIncomeDto.class))).thenReturn(income);

        mockMvc.perform(put("/api/incomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(1100.00));
    }

    @Test
    void testDeleteIncomesBulk() throws Exception {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        doNothing().when(incomeService).bulkDelete(any(BulkDeleteDto.class));

        mockMvc.perform(delete("/api/incomes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteIncomeSingle() throws Exception {
        doNothing().when(incomeService).deleteIncome(1L);

        mockMvc.perform(delete("/api/incomes/1"))
                .andExpect(status().isAccepted());
    }
}
