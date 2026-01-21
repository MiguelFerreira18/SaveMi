package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Category.SaveCategoryDto;
import com.money.SaveMi.Model.Category;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CategoryRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepo categoryRepo;
    private final UserRepo userRepo;
    private final AuthenticationServiceUtil authenticationServiceUtil;
    public CategoryService(CategoryRepo categoryRepo, UserRepo userRepo, AuthenticationServiceUtil authenticationServiceUtil) {
        this.categoryRepo = categoryRepo;
        this.userRepo = userRepo;
        this.authenticationServiceUtil = authenticationServiceUtil;
    }

    public Iterable<Category> getAllCategories(){
        String userUUID = authenticationServiceUtil.getCurrentUserUuid();
        return categoryRepo.findAllCategoriesByUserId(userUUID);
    }

    public Category getCategoryById(Long id){
        String userUUID = authenticationServiceUtil.getCurrentUserUuid();
        return categoryRepo.findCategoryByIdAndUserId(id, userUUID)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id + " for user: " + userUUID));

    }

    public Category saveCategory(SaveCategoryDto category){
        String userUUID = authenticationServiceUtil.getCurrentUserUuid();
        User user = userRepo.findById(userUUID)
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userUUID));
        Category newCategory = new Category(user, category.name(), category.description());
        return categoryRepo.save(newCategory);
    }

    public void deleteCategory(Long id){
        String userUUID = authenticationServiceUtil.getCurrentUserUuid();
        categoryRepo.deleteById(id, userUUID);
    }

    public Category findCategoryByNameDescriptionAndUserId(String name, String description, String userId ){
        return categoryRepo.findByNameDescriptionAndUserId(name, description, userId)
                .orElse(null);

    }


}
