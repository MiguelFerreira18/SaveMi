package com.money.SaveMi.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.money.SaveMi.DTO.Objective.SaveObjectiveDto;
import com.money.SaveMi.DTO.Objective.UpdateObjectiveDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Objective;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Service.ObjectiveService;
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
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ObjectiveControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ObjectiveService objectiveService;

    @InjectMocks
    private ObjectiveController objectiveController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Objective objective;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(objectiveController).build();
        
        User testUser = new User();
        testUser.setId("user-id");
        
        Currency currency = new Currency();
        currency.setSymbol("USD");
        
        objective = new Objective(testUser, currency, new BigDecimal("10000.00"), "Save for Car", 2025);
        objective.setId(1L);
    }

    @Test
    void testGetAllObjectives() throws Exception {
        when(objectiveService.getAllObjectives()).thenReturn(Collections.singletonList(objective));

        mockMvc.perform(get("/api/objectives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Save for Car"));
    }

    @Test
    void testGetObjectiveById() throws Exception {
        when(objectiveService.getObjectiveById(1L)).thenReturn(objective);

        mockMvc.perform(get("/api/objectives/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.target").value(2025));
    }

    @Test
    void testSaveObjective() throws Exception {
        SaveObjectiveDto dto = new SaveObjectiveDto(1L, new BigDecimal("10000.00"), "Save for Car", 2025);
        when(objectiveService.saveObjective(any(SaveObjectiveDto.class))).thenReturn(objective);

        mockMvc.perform(post("/api/objectives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Save for Car"));
    }

    @Test
    void testUpdateObjective() throws Exception {
        UpdateObjectiveDto dto = new UpdateObjectiveDto(1L, 1L, new BigDecimal("15000.00"), "Save for House", 2026);
        objective.setAmount(new BigDecimal("15000.00"));
        objective.setTarget(2026);
        when(objectiveService.updateObjective(any(UpdateObjectiveDto.class))).thenReturn(objective);

        mockMvc.perform(put("/api/objectives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(15000.00));
    }

    @Test
    void testDeleteObjectivesBulk() throws Exception {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        doNothing().when(objectiveService).bulkDelete(any(BulkDeleteDto.class));

        mockMvc.perform(delete("/api/objectives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteObjectiveSingle() throws Exception {
        doNothing().when(objectiveService).deleteObjective(1L);

        mockMvc.perform(delete("/api/objectives/1"))
                .andExpect(status().isAccepted());
    }
}
