package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Objective.SaveObjectiveDto;
import com.money.SaveMi.DTO.Objective.UpdateObjectiveDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Objective;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.ObjectiveRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ObjectiveServiceTest {

    @Mock
    private ObjectiveRepo objectiveRepo;
    @Mock
    private AuthenticationServiceUtil authUtil;
    @Mock
    private UserRepo userRepo;
    @Mock
    private CurrencyRepo currencyRepo;

    @InjectMocks
    private ObjectiveService objectiveService;

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
    void testGetAllObjectives() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(objectiveRepo.findAllByUserId(userUuid)).thenReturn(Arrays.asList(new Objective(), new Objective()));

        Iterable<Objective> result = objectiveService.getAllObjectives();

        assertNotNull(result);
        verify(objectiveRepo, times(1)).findAllByUserId(userUuid);
    }

    @Test
    void testGetObjectiveByIdSuccess() {
        Objective objective = new Objective();
        objective.setId(1L);
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(objectiveRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(objective));

        Objective result = objectiveService.getObjectiveById(1L);

        assertEquals(objective, result);
    }

    @Test
    void testSaveObjectiveSuccess() {
        SaveObjectiveDto dto = new SaveObjectiveDto(1L, new BigDecimal("10000"), "Save for Car", 2025);
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(userRepo.findById(userUuid)).thenReturn(Optional.of(testUser));
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testCurrency));
        when(objectiveRepo.save(any(Objective.class))).thenAnswer(i -> i.getArgument(0));

        Objective result = objectiveService.saveObjective(dto);

        assertNotNull(result);
        assertEquals(new BigDecimal("10000"), result.getAmount());
        assertEquals(2025, result.getTarget());
    }

    @Test
    void testUpdateObjectiveSuccess() {
        UpdateObjectiveDto dto = new UpdateObjectiveDto(1L, 1L, new BigDecimal("15000"), "Save for House", 2026);
        Objective oldObjective = new Objective();
        oldObjective.setId(1L);

        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(currencyRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(testCurrency));
        when(objectiveRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(oldObjective));
        when(objectiveRepo.save(any(Objective.class))).thenAnswer(i -> i.getArgument(0));

        Objective result = objectiveService.updateObjective(dto);

        assertEquals(new BigDecimal("15000"), result.getAmount());
        assertEquals(2026, result.getTarget());
    }

    @Test
    void testBulkDeleteSuccess() {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(objectiveRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Objective()));
        when(objectiveRepo.findByIdAndUserId(2L, userUuid)).thenReturn(Optional.of(new Objective()));

        objectiveService.bulkDelete(dto);

        verify(objectiveRepo, times(1)).bulkDelete(dto.ids(), userUuid);
    }

    @Test
    void testDeleteObjectiveSuccess() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(objectiveRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Objective()));

        objectiveService.deleteObjective(1L);

        verify(objectiveRepo, times(1)).deleteByIdAndUserId(1L, userUuid);
    }
}
