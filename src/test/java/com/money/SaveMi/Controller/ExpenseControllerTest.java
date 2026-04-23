package com.money.SaveMi.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.money.SaveMi.DTO.Expense.SaveExpenseDto;
import com.money.SaveMi.DTO.Expense.UpdateExpenseDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Category;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Expense;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Service.ExpenseService;
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
public class ExpenseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Expense expense;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();

        User testUser = new User();
        testUser.setId("user-id");

        Category category = new Category();
        category.setName("Food");

        Currency currency = new Currency();
        currency.setSymbol("USD");
        
        expense = new Expense(testUser, currency, category, new BigDecimal("50.00"), "Lunch", LocalDate.now());
        expense.setId(1L);
    }

    @Test
    void testGetAllExpenses() throws Exception {
        when(expenseService.getAllExpenses(any(Optional.class))).thenReturn(Collections.singletonList(expense));

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Lunch"));
    }

    @Test
    void testGetExpenseById() throws Exception {
        when(expenseService.getExpenseById(1L)).thenReturn(expense);

        mockMvc.perform(get("/api/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(50.00));
    }

    @Test
    void testSaveExpense() throws Exception {
        SaveExpenseDto dto = new SaveExpenseDto(1L, 1L, new BigDecimal("50.00"), "Lunch", LocalDate.now());
        when(expenseService.saveExpense(any(SaveExpenseDto.class))).thenReturn(expense);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Lunch"));
    }

    @Test
    void testUpdateExpense() throws Exception {
        UpdateExpenseDto dto = new UpdateExpenseDto(1L, 1L, 1L, new BigDecimal("60.00"), "Dinner", LocalDate.now());
        expense.setAmount(new BigDecimal("60.00"));
        expense.setDescription("Dinner");
        when(expenseService.updateExpense(any(UpdateExpenseDto.class))).thenReturn(expense);

        mockMvc.perform(put("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(60.00))
                .andExpect(jsonPath("$.description").value("Dinner"));
    }

    @Test
    void testDeleteExpensesBulk() throws Exception {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        doNothing().when(expenseService).bulkDelete(any(BulkDeleteDto.class));

        mockMvc.perform(delete("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteExpenseSingle() throws Exception {
        doNothing().when(expenseService).deleteExpense(1L);

        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isAccepted());
    }
}
