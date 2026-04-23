package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.DTO.StrategyType.SaveStrategyTypeDto;
import com.money.SaveMi.Model.StrategyType;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.StrategyTypeRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StrategyTypeServiceTest {

    @Mock
    private StrategyTypeRepo strategyTypeRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private AuthenticationServiceUtil authUtil;

    @InjectMocks
    private StrategyTypeService strategyTypeService;

    private User testUser;
    private String userUuid = "test-uuid";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userUuid);
    }

    @Test
    void testGetAllStrategyTypes() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(strategyTypeRepo.findAllByUserId(userUuid)).thenReturn(Arrays.asList(new StrategyType(), new StrategyType()));

        Iterable<StrategyType> result = strategyTypeService.getAllStrategyTypes();

        assertNotNull(result);
        verify(strategyTypeRepo, times(1)).findAllByUserId(userUuid);
    }

    @Test
    void testGetStrategyTypeByIdSuccess() {
        StrategyType st = new StrategyType();
        st.setId(1L);
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(strategyTypeRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(st));

        StrategyType result = strategyTypeService.getStrategyTypeById(1L);

        assertEquals(st, result);
    }

    @Test
    void testSaveStrategyTypeSuccess() {
        SaveStrategyTypeDto dto = new SaveStrategyTypeDto("Aggressive", "High risk");
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(userRepo.findById(userUuid)).thenReturn(Optional.of(testUser));
        when(strategyTypeRepo.save(any(StrategyType.class))).thenAnswer(i -> i.getArgument(0));

        StrategyType result = strategyTypeService.saveStrategyType(dto);

        assertNotNull(result);
        assertEquals("Aggressive", result.getName());
    }

    @Test
    void testBulkDeleteSuccess() {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(strategyTypeRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new StrategyType()));
        when(strategyTypeRepo.findByIdAndUserId(2L, userUuid)).thenReturn(Optional.of(new StrategyType()));

        strategyTypeService.bulkDelete(dto);

        verify(strategyTypeRepo, times(1)).bulkDelete(dto.ids(), userUuid);
    }

    @Test
    void testDeleteStrategyTypeSuccess() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(strategyTypeRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new StrategyType()));

        strategyTypeService.deleteStrategyType(1L);

        verify(strategyTypeRepo, times(1)).deleteById(1L, userUuid);
    }

    @Test
    void testFindStrategyTypeByNameDescriptionAndUserId() {
        StrategyType st = new StrategyType();
        when(strategyTypeRepo.findByNameAndDescriptionAndUserId("Name", "Desc", userUuid)).thenReturn(Optional.of(st));

        StrategyType result = strategyTypeService.findStrategyTypeByNameDescriptionAndUserId("Name", "Desc", userUuid);

        assertEquals(st, result);
    }
}
