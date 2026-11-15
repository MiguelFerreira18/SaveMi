package com.money.SaveMi.Integration.Service;

import com.money.SaveMi.DTO.Objective.SaveObjectiveDto;
import com.money.SaveMi.DTO.Objective.UpdateObjectiveDto;
import com.money.SaveMi.Integration.BaseTest;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Objective;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.ObjectiveRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Service.ObjectiveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObjectiveServiceTest extends BaseTest {
    @Autowired
    private ObjectiveService objectiveService;

    @Autowired
    private ObjectiveRepo objectiveRepo;

    @Autowired
    private CurrencyRepo currencyRepo;
    @Autowired
    private UserRepo userRepo;

    private Currency testCurrency;
    private User testUser;

    @BeforeEach
    void setUp() {
        objectiveRepo.deleteAll();
        currencyRepo.deleteAll();
        userRepo.deleteAll();

        testUser = new User("test@example.com", "testuser", "Password123!");
        testUser = userRepo.save(testUser);

        testCurrency = new Currency(testUser, "euro", "EUR");
        testCurrency = currencyRepo.save(testCurrency);

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
            "testObjective1, 12.10, 2030",
            "testObjective2, 12030.60, 2050",
            "testObjective3, 20000.00,2090",
    })
    @DisplayName("Should be able to add new objectives")
    void testSaveObjective(String description, BigDecimal amount, int target) {
        SaveObjectiveDto saveObjectiveDto = new SaveObjectiveDto(testCurrency.getId(), amount, description, target);

        Objective objective = objectiveService.saveObjective(saveObjectiveDto);

        assertNotNull(objective.getId());
        assertEquals(description, objective.getDescription());
        assertEquals(amount, objective.getAmount());
        assertEquals(testCurrency.getId(), objective.getCurrency().getId());
        assertEquals(testUser.getId(), objective.getUser().getId());

        Optional<Objective> found = objectiveRepo.findById(objective.getId());

        assertTrue(found.isPresent());
        assertEquals(description, found.get().getDescription());
        assertEquals(amount, found.get().getAmount());
        assertEquals(target, found.get().getTarget());
        assertEquals(testCurrency.getId(), objective.getCurrency().getId());
    }

    @Test
    void testGetAllObjectives() {
        SaveObjectiveDto firstSaveWishDto = new SaveObjectiveDto(testCurrency.getId(), BigDecimal.valueOf(12.1), "testObjective1", 2030);
        SaveObjectiveDto secondSaveWishDto = new SaveObjectiveDto(testCurrency.getId(), BigDecimal.valueOf(133000.1), "testObjective2", 2050);
        SaveObjectiveDto thirdSaveWishDto = new SaveObjectiveDto(testCurrency.getId(), BigDecimal.valueOf(2000), "testObjective3", 2090);

        objectiveService.saveObjective(firstSaveWishDto);
        objectiveService.saveObjective(secondSaveWishDto);
        objectiveService.saveObjective(thirdSaveWishDto);

        List<Objective> objectives = StreamSupport.stream(objectiveRepo.findAll().spliterator(), false).toList();

        assertEquals(3, objectives.size());
        assertTrue(objectives.stream().anyMatch(w -> w.getDescription().equals("testObjective1")));
        assertTrue(objectives.stream().anyMatch(w -> w.getDescription().equals("testObjective2")));
        assertTrue(objectives.stream().anyMatch(w -> w.getDescription().equals("testObjective3")));
        assertFalse(objectives.stream().anyMatch(w -> w.getDescription().equals("testObjective4")));
        assertEquals(1, objectives.stream().filter(w -> w.getDescription().equals("testObjective1")).count());
        assertEquals(1, objectives.stream().filter(w -> w.getDescription().equals("testObjective2")).count());
        assertEquals(1, objectives.stream().filter(w -> w.getDescription().equals("testObjective3")).count());
    }
    @ParameterizedTest
    @CsvSource({
            "testObjective1, 12.10, 2030",
            "testObjective2, 12030.60, 2050",
            "testObjective3, 20000.00,2090",
    })
    void testUpdateObjective(String description, BigDecimal amount, int target) {
        SaveObjectiveDto objectiveDto = new SaveObjectiveDto(testCurrency.getId(),  BigDecimal.valueOf(1),"standard", target);
        Objective objective = objectiveService.saveObjective(objectiveDto);

        UpdateObjectiveDto updateObjectiveDto = new UpdateObjectiveDto(objective.getId(),testCurrency.getId(), amount,description, target);

        Objective updateObjective = objectiveService.updateObjective(updateObjectiveDto);
        assertEquals(objective.getId(),updateObjective.getId());
        assertEquals(description, updateObjective.getDescription());
        assertEquals(amount, updateObjective.getAmount());
        assertEquals(testUser.getId(), updateObjective.getUser().getId());
        assertEquals(testCurrency.getId(), updateObjective.getCurrency().getId());

        Optional<Objective> found = objectiveRepo.findById(updateObjective.getId());
        assertTrue(found.isPresent());
        assertEquals(description, found.get().getDescription());
        assertEquals(amount, found.get().getAmount());
        assertEquals(updateObjectiveDto.target(), found.get().getTarget());
    }

    @ParameterizedTest
    @CsvSource({
            "testObjective1, 12.10, 2030",
            "testObjective2, 12030.60, 2050",
            "testObjective3, 20000.00,2090",
    })
    void testDeleteObjective(String description, BigDecimal amount, int target) {
        SaveObjectiveDto objectiveDto = new SaveObjectiveDto(testCurrency.getId(), amount, description, target);

        Objective objective = objectiveService.saveObjective(objectiveDto);
        assertFalse(StreamSupport.stream(objectiveRepo.findAll().spliterator(), false).toList().isEmpty());
        objectiveService.deleteObjective(objective.getId());
        assertTrue(StreamSupport.stream(objectiveRepo.findAll().spliterator(), false).toList().isEmpty());
    }
}
