package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Category.SaveCategoryDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
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
    private final AuthenticationServiceUtil authUtil;
    public CategoryService(CategoryRepo categoryRepo, UserRepo userRepo, AuthenticationServiceUtil authUtil) {
        this.categoryRepo = categoryRepo;
        this.userRepo = userRepo;
        this.authUtil = authUtil;
    }

    public Iterable<Category> getAllCategories(){
        String userUUID = authUtil.getCurrentUserUuid();
        return categoryRepo.findAllCategoriesByUserId(userUUID);
    }

    public Category getCategoryById(Long id){
        String userUUID = authUtil.getCurrentUserUuid();
        return categoryRepo.findCategoryByIdAndUserId(id, userUUID)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id + " for user: " + userUUID));

    }

    public Category saveCategory(SaveCategoryDto category){
        String userUUID = authUtil.getCurrentUserUuid();
        User user = userRepo.findById(userUUID)
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userUUID));
        Category newCategory = new Category(user, category.name(), category.description());
        return categoryRepo.save(newCategory);
    }

    public void bulkDelete(BulkDeleteDto bulkdeleteDto){
        String userId = authUtil.getCurrentUserUuid();

        bulkdeleteDto.ids().forEach(id -> {
            if(categoryRepo.findCategoryByIdAndUserId(id,userId).isEmpty()){
                throw new RuntimeException("Category not found with id: " + id + " for user: " + userId + " in bulk");
            }
        });

        categoryRepo.bulkDelete(bulkdeleteDto.ids(),userId);
    }

    public void deleteCategory(Long id){
        String userUUID = authUtil.getCurrentUserUuid();

        if(categoryRepo.findCategoryByIdAndUserId(id,userUUID).isEmpty()){
            throw new RuntimeException("Category not found with id: " + id + " for user: " + userUUID);
        }

        categoryRepo.deleteById(id, userUUID);
    }

    public Category findCategoryByNameDescriptionAndUserId(String name, String description, String userId ){
        return categoryRepo.findByNameDescriptionAndUserId(name, description, userId)
                .orElse(null);

    }


}
