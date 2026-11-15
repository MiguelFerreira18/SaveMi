package com.money.SaveMi.Integration.Service;

import com.money.SaveMi.DTO.Category.SaveCategoryDto;
import com.money.SaveMi.Integration.BaseTest;
import com.money.SaveMi.Model.Category;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CategoryRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Service.CategoryService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryServiceTest extends BaseTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private UserRepo userRepo;

    private User testUser;

    @BeforeEach
    void setUp() {
        categoryRepo.deleteAll();
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
            "Subscription, subscription category",
            "Food, food category",
            "Water, water category",
            "Electricity, electricity category",
    })
    void testSaveCategory(String name, String description) {
        SaveCategoryDto saveCategoryDto = new SaveCategoryDto(name, description);

        Category category = categoryService.saveCategory(saveCategoryDto);

        assertNotNull(category.getId());
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
        assertEquals(testUser.getId(), category.getUser().getId());

        Optional<Category> found = categoryRepo.findById(category.getId());
        assertTrue(found.isPresent());
        assertEquals(name, found.get().getName());
        assertEquals(description, found.get().getDescription());
    }

    @Test
    void testGetAllCategories() {
        SaveCategoryDto firstSaveCategoryDto = new SaveCategoryDto("Subscription", "subscription category");
        SaveCategoryDto secondSaveCategoryDto = new SaveCategoryDto("Food", "food category");
        SaveCategoryDto thirdSaveCategoryDto = new SaveCategoryDto("Water", "water category");
        SaveCategoryDto fourthSaveCategoryDto = new SaveCategoryDto("Electricity", "electricity category");

        categoryService.saveCategory(firstSaveCategoryDto);
        categoryService.saveCategory(secondSaveCategoryDto);
        categoryService.saveCategory(thirdSaveCategoryDto);
        categoryService.saveCategory(fourthSaveCategoryDto);

        List<Category> categories = StreamSupport.stream(categoryRepo.findAll().spliterator(), false).toList();

        assertEquals(4, categories.size());
        assertTrue(categories.stream().anyMatch(s -> s.getName().equals("Subscription")));
        assertTrue(categories.stream().anyMatch(s -> s.getName().equals("Food")));
        assertTrue(categories.stream().anyMatch(s -> s.getName().equals("Water")));
        assertTrue(categories.stream().anyMatch(s -> s.getName().equals("Electricity")));
        assertEquals(1, categories.stream().filter(s -> s.getName().equals("Subscription")).count());
        assertEquals(1, categories.stream().filter(s -> s.getName().equals("Food")).count());
        assertEquals(1, categories.stream().filter(s -> s.getName().equals("Water")).count());
        assertEquals(1, categories.stream().filter(s -> s.getName().equals("Electricity")).count());
    }

    @ParameterizedTest
    @CsvSource({
            "Subscription, subscription category",
            "Food, food category",
            "Water, water category",
            "Electricity, electricity category",
    })
    void testDeleteCategory(String name, String description) {
        SaveCategoryDto saveCategoryDto = new SaveCategoryDto(name, description);

        Category category = categoryService.saveCategory(saveCategoryDto);

        assertFalse(StreamSupport.stream(categoryRepo.findAll().spliterator(), false).toList().isEmpty());
        categoryService.deleteCategory(category.getId());
        assertTrue(StreamSupport.stream(categoryRepo.findAll().spliterator(), false).toList().isEmpty());


    }
}
