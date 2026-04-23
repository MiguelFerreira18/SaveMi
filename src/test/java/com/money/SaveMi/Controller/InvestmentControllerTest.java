package com.money.SaveMi.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.money.SaveMi.DTO.Investment.SaveInvestmentDto;
import com.money.SaveMi.DTO.Investment.UpdateInvestmentDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Investment;
import com.money.SaveMi.Model.StrategyType;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Service.InvestmentService;
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
public class InvestmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InvestmentService investmentService;

    @InjectMocks
    private InvestmentController investmentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Investment investment;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(investmentController).build();

        User testUser = new User();
        testUser.setId("user-id");

        Currency currency = new Currency();
        currency.setSymbol("USD");

        StrategyType strategyType = new StrategyType();
        strategyType.setName("Stocks");
        
        investment = new Investment(testUser, currency, new BigDecimal("1000.00"), strategyType, "Apple Stocks", LocalDate.now());
        investment.setId(1L);
    }

    @Test
    void testGetAllInvestments() throws Exception {
        when(investmentService.getAllInvestments(any(Optional.class))).thenReturn(Collections.singletonList(investment));

        mockMvc.perform(get("/api/investments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Apple Stocks"));
    }

    @Test
    void testGetInvestmentById() throws Exception {
        when(investmentService.getInvestmentById(1L)).thenReturn(investment);

        mockMvc.perform(get("/api/investments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(1000.00));
    }

    @Test
    void testSaveInvestment() throws Exception {
        SaveInvestmentDto dto = new SaveInvestmentDto(1L, 1L, new BigDecimal("1000.00"), "Apple Stocks", LocalDate.now());
        when(investmentService.saveInvestment(any(SaveInvestmentDto.class))).thenReturn(investment);

        mockMvc.perform(post("/api/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Apple Stocks"));
    }

    @Test
    void testUpdateInvestment() throws Exception {
        UpdateInvestmentDto dto = new UpdateInvestmentDto(1L, 1L, 1L, new BigDecimal("1200.00"), "Apple Stocks more", LocalDate.now());
        investment.setAmount(new BigDecimal("1200.00"));
        when(investmentService.updateInvestment(any(UpdateInvestmentDto.class))).thenReturn(investment);

        mockMvc.perform(put("/api/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(1200.00));
    }

    @Test
    void testDeleteInvestmentsBulk() throws Exception {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        doNothing().when(investmentService).bulkDelete(any(BulkDeleteDto.class));

        mockMvc.perform(delete("/api/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteInvestmentSingle() throws Exception {
        doNothing().when(investmentService).deleteInvestment(1L);

        mockMvc.perform(delete("/api/investments/1"))
                .andExpect(status().isAccepted());
    }
}
