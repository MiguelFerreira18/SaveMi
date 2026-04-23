package com.money.SaveMi.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.DTO.StrategyType.SaveStrategyTypeDto;
import com.money.SaveMi.Model.StrategyType;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Service.StrategyTypeService;
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
public class StrategyTypeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StrategyTypeService strategyTypeService;

    @InjectMocks
    private StrategyTypeController strategyTypeController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private StrategyType strategyType;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(strategyTypeController).build();
        User testUser = new User();
        testUser.setId("user-id");
        strategyType = new StrategyType(testUser, "Aggressive", "High risk");
        strategyType.setId(1L);
    }

    @Test
    void testGetAllStrategyTypes() throws Exception {
        when(strategyTypeService.getAllStrategyTypes()).thenReturn(Collections.singletonList(strategyType));

        mockMvc.perform(get("/api/strategies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Aggressive"));
    }

    @Test
    void testGetStrategyTypeById() throws Exception {
        when(strategyTypeService.getStrategyTypeById(1L)).thenReturn(strategyType);

        mockMvc.perform(get("/api/strategies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Aggressive"));
    }

    @Test
    void testSaveStrategyType() throws Exception {
        SaveStrategyTypeDto dto = new SaveStrategyTypeDto("Aggressive", "High risk");
        when(strategyTypeService.saveStrategyType(any(SaveStrategyTypeDto.class))).thenReturn(strategyType);

        mockMvc.perform(post("/api/strategies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aggressive"));
    }

    @Test
    void testDeleteStrategyTypesBulk() throws Exception {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        doNothing().when(strategyTypeService).bulkDelete(any(BulkDeleteDto.class));

        mockMvc.perform(delete("/api/strategies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteStrategyTypeSingle() throws Exception {
        doNothing().when(strategyTypeService).deleteStrategyType(1L);

        mockMvc.perform(delete("/api/strategies/1"))
                .andExpect(status().isAccepted());
    }
}
