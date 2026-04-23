package com.money.SaveMi.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.DTO.Wish.SaveWishDto;
import com.money.SaveMi.DTO.Wish.UpdateWishDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Model.Wish;
import com.money.SaveMi.Service.WishService;
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
public class WishControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WishService wishService;

    @InjectMocks
    private WishController wishController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Wish wish;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(wishController).build();

        User testUser = new User();
        testUser.setId("user-id");

        Currency currency = new Currency();
        currency.setSymbol("USD");
        
        wish = new Wish(testUser, currency, new BigDecimal("500.00"), "New Bike", LocalDate.now());
        wish.setId(1L);
    }

    @Test
    void testGetAllWishes() throws Exception {
        when(wishService.getAllWishes(any(Optional.class))).thenReturn(Collections.singletonList(wish));

        mockMvc.perform(get("/api/wishes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("New Bike"));
    }

    @Test
    void testGetWishById() throws Exception {
        when(wishService.getWishById(1L)).thenReturn(wish);

        mockMvc.perform(get("/api/wishes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(500.00));
    }

    @Test
    void testSaveWish() throws Exception {
        SaveWishDto dto = new SaveWishDto(1L, "New Bike", new BigDecimal("500.00"), LocalDate.now());
        when(wishService.saveWish(any(SaveWishDto.class))).thenReturn(wish);

        mockMvc.perform(post("/api/wishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("New Bike"));
    }

    @Test
    void testUpdateWish() throws Exception {
        UpdateWishDto dto = new UpdateWishDto(1L, 1L, "New Bike v2", new BigDecimal("550.00"), LocalDate.now());
        wish.setAmount(new BigDecimal("550.00"));
        wish.setDescription("New Bike v2");
        when(wishService.updateWish(any(UpdateWishDto.class))).thenReturn(wish);

        mockMvc.perform(put("/api/wishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(550.00));
    }

    @Test
    void testDeleteWishesBulk() throws Exception {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        doNothing().when(wishService).bulkDelete(any(BulkDeleteDto.class));

        mockMvc.perform(delete("/api/wishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteWishSingle() throws Exception {
        doNothing().when(wishService).deleteWishById(1L);

        mockMvc.perform(delete("/api/wishes/1"))
                .andExpect(status().isNoContent());
    }
}
