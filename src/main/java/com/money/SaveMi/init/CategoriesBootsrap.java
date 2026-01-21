package com.money.SaveMi.init;

import com.money.SaveMi.DTO.Category.SaveCategoryDto;
import com.money.SaveMi.Model.Category;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CategoryRepo;
import com.money.SaveMi.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(BootstrapOrder.CATEGORIES)
@Profile("dev")
public class CategoriesBootsrap implements CommandLineRunner {
    private final UserService userService;
    private final CategoryRepo categoryRepo;

    public CategoriesBootsrap(UserService userService, CategoryRepo categoryRepo) {
        this.userService = userService;
        this.categoryRepo = categoryRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        User u1 = userService.getUserByEmail("coisa@gmail.com");
        User u2 = userService.getUserByEmail("coisa2@gmail.com");

        SaveCategoryDto subscription = new SaveCategoryDto("Subscription", "Monthly subscriptions");
        SaveCategoryDto food = new SaveCategoryDto("Food", "Groceries and dining out");
        SaveCategoryDto entertainment = new SaveCategoryDto("Entertainment", "Movies, concerts, and more");
        SaveCategoryDto utilities = new SaveCategoryDto("Utilities", "Electricity");

        saveCategory(u1, subscription );
        saveCategory(u1, entertainment);
        saveCategory(u1, food);
        saveCategory(u2, subscription);
        saveCategory(u2, entertainment);
        saveCategory(u2, utilities);

    }

    private void saveCategory(User u, SaveCategoryDto categoryDto) {
        if (categoryRepo.findByNameDescriptionAndUserId(categoryDto.name(),categoryDto.description(), u.getId()).isEmpty()) {
            categoryRepo.save(new Category(u, categoryDto.name(), categoryDto.description()));
        }

    }
}
