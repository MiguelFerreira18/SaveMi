package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Category.SaveCategoryDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Category;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CategoryRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private AuthenticationServiceUtil authUtil;

    @InjectMocks
    private CategoryService categoryService;

    private User testUser;
    private String userUuid = "test-uuid";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userUuid);
    }

    @Test
    void testGetAllCategories() {
        Category cat1 = new Category(testUser, "Cat 1", "Desc 1");
        Category cat2 = new Category(testUser, "Cat 2", "Desc 2");
        
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(categoryRepo.findAllByUserId(userUuid)).thenReturn(Arrays.asList(cat1, cat2));

        Iterable<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        int count = 0;
        for (Category ignored : result) count++;
        assertEquals(2, count);
        verify(categoryRepo, times(1)).findAllByUserId(userUuid);
    }

    @Test
    void testGetCategoryByIdSuccess() {
        Category cat = new Category(testUser, "Cat 1", "Desc 1");
        cat.setId(1L);
        
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(categoryRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(cat));

        Category result = categoryService.getCategoryById(1L);

        assertEquals(cat, result);
    }

    @Test
    void testGetCategoryByIdNotFound() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(categoryRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void testSaveCategorySuccess() {
        SaveCategoryDto dto = new SaveCategoryDto("New Cat", "New Desc");
        
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(userRepo.findById(userUuid)).thenReturn(Optional.of(testUser));
        when(categoryRepo.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.saveCategory(dto);

        assertNotNull(result);
        assertEquals("New Cat", result.getName());
        assertEquals(testUser, result.getUser());
    }

    @Test
    void testSaveCategoryUserNotFound() {
        SaveCategoryDto dto = new SaveCategoryDto("New Cat", "New Desc");
        
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(userRepo.findById(userUuid)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> categoryService.saveCategory(dto));
    }

    @Test
    void testBulkDeleteSuccess() {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(categoryRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Category()));
        when(categoryRepo.findByIdAndUserId(2L, userUuid)).thenReturn(Optional.of(new Category()));

        categoryService.bulkDelete(dto);

        verify(categoryRepo, times(1)).bulkDelete(dto.ids(), userUuid);
    }

    @Test
    void testBulkDeleteNotFound() {
        BulkDeleteDto dto = new BulkDeleteDto(Arrays.asList(1L, 2L));
        
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(categoryRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.bulkDelete(dto));
    }

    @Test
    void testDeleteCategorySuccess() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(categoryRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.of(new Category()));

        categoryService.deleteCategory(1L);

        verify(categoryRepo, times(1)).deleteById(1L, userUuid);
    }

    @Test
    void testDeleteCategoryNotFound() {
        when(authUtil.getCurrentUserUuid()).thenReturn(userUuid);
        when(categoryRepo.findByIdAndUserId(1L, userUuid)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.deleteCategory(1L));
    }

    @Test
    void testFindCategoryByNameDescriptionAndUserId() {
        Category cat = new Category(testUser, "Name", "Desc");
        
        when(categoryRepo.findByNameDescriptionAndUserId("Name", "Desc", userUuid)).thenReturn(Optional.of(cat));

        Category result = categoryService.findCategoryByNameDescriptionAndUserId("Name", "Desc", userUuid);

        assertEquals(cat, result);
    }
}
