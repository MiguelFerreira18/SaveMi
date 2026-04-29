package com.money.SaveMi.Integration.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.money.SaveMi.DTO.Category.SaveCategoryDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Integration.BaseTest;
import com.money.SaveMi.Model.Category;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CategoryRepo;
import com.money.SaveMi.Repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class CategoryControllerIntegrationTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void testGetAllCategories() throws Exception {
        categoryRepo.save(new Category(testUser, "Subscription", "subscription category"));
        categoryRepo.save(new Category(testUser, "Food", "food category"));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.name == 'Subscription')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Food')]").exists());
    }

    @Test
    void testGetCategoryById() throws Exception {
        Category category = categoryRepo.save(new Category(testUser, "Subscription", "subscription category"));

        mockMvc.perform(get("/api/categories/" + category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value("Subscription"));
    }

    @Test
    void testSaveCategory() throws Exception {
        SaveCategoryDto dto = new SaveCategoryDto("New Category", "New Description");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Category"))
                .andExpect(jsonPath("$.description").value("New Description"));
    }

    @Test
    void testDeleteCategorySingle() throws Exception {
        Category category = categoryRepo.save(new Category(testUser, "To Delete", "Delete me"));

        mockMvc.perform(delete("/api/categories/" + category.getId()))
                .andExpect(status().isAccepted());

        assert(categoryRepo.findById(category.getId()).isEmpty());
    }

    @Test
    void testDeleteCategoriesBulk() throws Exception {
        Category cat1 = categoryRepo.save(new Category(testUser, "Cat 1", "Desc 1"));
        Category cat2 = categoryRepo.save(new Category(testUser, "Cat 2", "Desc 2"));

        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(cat1.getId(), cat2.getId()));

        mockMvc.perform(delete("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        assert(categoryRepo.findById(cat1.getId()).isEmpty());
        assert(categoryRepo.findById(cat2.getId()).isEmpty());
    }
}
