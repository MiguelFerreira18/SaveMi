package com.money.SaveMi.init;

import com.money.SaveMi.Model.Category;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Expense;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.ExpenseRepo;
import com.money.SaveMi.Service.CategoryService;
import com.money.SaveMi.Service.CurrencyService;
import com.money.SaveMi.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Component
@Order(BootstrapOrder.EXPENSES)
@Profile("dev")
public class ExpenseBootstrap implements CommandLineRunner {
    private final UserService userService;
    private final CurrencyService currencyService;
    private final CategoryService categoryService;
    private final ExpenseRepo expenseRepo;

    public ExpenseBootstrap(UserService userService, CurrencyService currencyService, CategoryService categoryService, ExpenseRepo expenseRepo) {
        this.userService = userService;
        this.currencyService = currencyService;
        this.categoryService = categoryService;
        this.expenseRepo = expenseRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        User u1 = userService.getUserByEmail("coisa@gmail.com");
        Category subscription = categoryService.findCategoryByNameDescriptionAndUserId("Subscription", "Monthly subscriptions", u1.getId());
        Category food = categoryService.findCategoryByNameDescriptionAndUserId("Food", "Groceries and dining out", u1.getId());
        Currency eur = currencyService.findByNameAndSymbolAndUserId("EUR", "€", u1.getId());
        Currency usd = currencyService.findByNameAndSymbolAndUserId("USD", "$", u1.getId());

        User u2 = userService.getUserByEmail("coisa2@gmail.com");
        Currency eur2 = currencyService.findByNameAndSymbolAndUserId("EUR", "€", u2.getId());
        Category entertainment = categoryService.findCategoryByNameDescriptionAndUserId("Entertainment", "Movies, concerts, and more", u2.getId());

        saveExpense(u1, eur, subscription, 5);
        saveExpense(u1, usd, food, 3);
        saveExpense(u2, eur2, entertainment, 4);

    }

    private void saveExpense(User u, Currency c, Category category, int numberOfExpenses) {
        for(int i = 1; i <= numberOfExpenses; i++){
            BigDecimal value = new BigDecimal( i * 100.23).setScale(2, RoundingMode.HALF_UP);
            final String description = String.format("EXPENSE%d", i);
            if (expenseRepo.findExpenseByUserIdCurrencyCategoryAndAmount(u.getId(),c.getId(),category.getId(), description,value).isEmpty()) {
                Expense expense = new Expense(u, c, category, value, description, LocalDate.now().minusDays(i));
                expenseRepo.save(expense);
            }
        }
    }
}
