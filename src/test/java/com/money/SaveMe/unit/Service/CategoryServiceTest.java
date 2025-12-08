package com.money.SaveMe.unit.Service;

import com.money.SaveMe.Model.Category;
import com.money.SaveMe.Model.User;
import com.money.SaveMe.Repo.CategoryRepo;
import com.money.SaveMe.Service.CategoryService;
import com.money.SaveMe.Utils.AuthenticationServiceUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private AuthenticationServiceUtil authenticationServiceUtil;
    @Mock
    private CategoryRepo categoryRepo;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void testGetAllCategories(){
        UUID uuid = UUID.randomUUID();
        String userId = uuid.toString();
        String email = "user@gmail.com";
        String name = "user";
        String password = "password";
        Instant now = Instant.now();
        Instant newNow = Instant.now();
        User u = new User(userId,email,name,password, new ArrayList<>(),now,newNow);

        Long id = 1L;
        String catName = "category";
        String description = "New description";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Category cat = new Category(id,u,catName,description,createdAt,updatedAt);

        List<Category> catList = new ArrayList<>();
        catList.add(cat);
        Mockito.when(authenticationServiceUtil.getCurrentUserUuid()).thenReturn(userId);
        Mockito.when(categoryService.getAllCategories()).thenReturn(List.of(cat));

        Iterable<Category> returnedCatList = categoryService.getAllCategories();

        assertNotNull(returnedCatList);
        assertTrue(returnedCatList.iterator().hasNext());
        Category returnedCategory = returnedCatList.iterator().next();
        assertEquals(1L, returnedCategory.getId());
        assertEquals(catName, returnedCategory.getName());
        assertEquals(description, returnedCategory.getDescription());
        assertEquals(createdAt, returnedCategory.getCreatedAt());
        assertEquals(updatedAt, returnedCategory.getUpdatedAt());
        assertEquals(userId, returnedCategory.getUser().getId());
    }

    @Test
    public void testGetCategoryById(){
        UUID uuid = UUID.randomUUID();
        String userId = uuid.toString();
        String email = "user@gmail.com";
        String name = "user";
        String password = "password";
        Instant now = Instant.now();
        Instant newNow = Instant.now();
        User u = new User(userId,email,name,password, new ArrayList<>(),now,newNow);
        Long id = 1L;
        String catName = "category";
        String description = "New description";
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();
        Category cat = new Category(id,u,catName,description,createdAt,updatedAt);


        Mockito.when(authenticationServiceUtil.getCurrentUserUuid()).thenReturn(userId);
        Mockito.when(categoryService.getCategoryById(id)).thenReturn(cat);

        assertNotNull(categoryService.getCategoryById(id));
        assertEquals(categoryService.getCategoryById(id), cat);
        assertEquals(id, cat.getId());
        assertEquals(catName,  cat.getName());
        assertEquals(description,  cat.getDescription());
        assertEquals(createdAt,  cat.getCreatedAt());
        assertEquals(updatedAt,  cat.getUpdatedAt());
        assertEquals(userId, cat.getUser().getId());
        }
}