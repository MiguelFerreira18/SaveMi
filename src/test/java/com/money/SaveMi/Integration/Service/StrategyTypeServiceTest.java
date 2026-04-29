package com.money.SaveMi.Integration.Service;

import com.money.SaveMi.DTO.StrategyType.SaveStrategyTypeDto;
import com.money.SaveMi.Integration.BaseTest;
import com.money.SaveMi.Model.StrategyType;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.StrategyTypeRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Service.StrategyTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class StrategyTypeServiceTest extends BaseTest {
    @Autowired
    private StrategyTypeService strategyTypeService;

    @Autowired
    private StrategyTypeRepo strategyTypeRepo;

    @Autowired
    private UserRepo userRepo;

    private User testUser;

    @BeforeEach
    void setUp() {
        strategyTypeRepo.deleteAll();
        userRepo.deleteAll();

        testUser = new User("test@example.com", "testuser", "Password123!");
        testUser = userRepo.save(testUser);

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
            "High risk, savings based on high risk investments",
            "Medium risk, savings based on medium risk investments",
            "Low risk, savings based on low risk investment",
            "ETF, savings based on only ETF's",
    })
    void testSaveStrategyTypes(String name, String description) {
        SaveStrategyTypeDto saveStrategyTypeDto = new SaveStrategyTypeDto(name, description);

        StrategyType strategyType = strategyTypeService.saveStrategyType(saveStrategyTypeDto);

        assertNotNull(strategyType.getId());
        assertEquals(name, strategyType.getName());
        assertEquals(description, strategyType.getDescription());
        assertEquals(testUser.getId(), strategyType.getUser().getId());

        Optional<StrategyType> found = strategyTypeRepo.findById(strategyType.getId());
        assertTrue(found.isPresent());
        assertEquals(name, found.get().getName());
        assertEquals(description, found.get().getDescription());
    }

    @Test
    void testGetAllStrategies(){
        SaveStrategyTypeDto firstSaveStrategyTypeDto = new SaveStrategyTypeDto("High risk", "savings based on high risk investments");
        SaveStrategyTypeDto secondSaveStrategyTypeDto = new SaveStrategyTypeDto("Medium risk", "savings based on medium risk investments");
        SaveStrategyTypeDto thirdSaveStrategyTypeDto = new SaveStrategyTypeDto("Low risk","savings based on low risk investment");
        SaveStrategyTypeDto fourthSaveStrategyTypeDto = new SaveStrategyTypeDto("ETF","savings based on only ETF's");

        strategyTypeService.saveStrategyType(firstSaveStrategyTypeDto);
        strategyTypeService.saveStrategyType(secondSaveStrategyTypeDto);
        strategyTypeService.saveStrategyType(thirdSaveStrategyTypeDto);
        strategyTypeService.saveStrategyType(fourthSaveStrategyTypeDto);

        List<StrategyType> strategyTypes = StreamSupport.stream(strategyTypeRepo.findAll().spliterator(),false).toList();

        assertEquals(4, strategyTypes.size());
        assertTrue(strategyTypes.stream().anyMatch( s -> s.getName().equals("High risk")));
        assertTrue(strategyTypes.stream().anyMatch( s -> s.getName().equals("Medium risk")));
        assertTrue(strategyTypes.stream().anyMatch( s -> s.getName().equals("Low risk")));
        assertTrue(strategyTypes.stream().anyMatch( s -> s.getName().equals("ETF")));
        assertEquals(1, strategyTypes.stream().filter(s -> s.getName().equals("High risk")).count());
        assertEquals(1, strategyTypes.stream().filter(s -> s.getName().equals("Medium risk")).count());
        assertEquals(1, strategyTypes.stream().filter(s -> s.getName().equals("Low risk")).count());
        assertEquals(1, strategyTypes.stream().filter(s -> s.getName().equals("ETF")).count());
    }

    @ParameterizedTest
    @CsvSource({
            "High risk, savings based on high risk investments",
            "Medium risk, savings based on medium risk investments",
            "Low risk, savings based on low risk investment",
            "ETF, savings based on only ETF's",
    })
    void testDeleteStrategyType(String name, String description){
        SaveStrategyTypeDto saveStrategyTypeDto = new SaveStrategyTypeDto(name, description);

        StrategyType strategyType = strategyTypeService.saveStrategyType(saveStrategyTypeDto);

        assertFalse(StreamSupport.stream(strategyTypeRepo.findAll().spliterator(),false).toList().isEmpty());
        strategyTypeService.deleteStrategyType(strategyType.getId());
        assertTrue(StreamSupport.stream(strategyTypeRepo.findAll().spliterator(),false).toList().isEmpty());




    }

}
