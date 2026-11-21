package com.money.SaveMi.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.money.SaveMi.DTO.Expense.SaveExpenseDto;
import com.money.SaveMi.DTO.Expense.UpdateExpenseDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Filter.ExpenseFilter;
import com.money.SaveMi.Model.Category;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Expense;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CategoryRepo;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.ExpenseRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;

@Service
public class ExpenseService {
    private final ExpenseRepo expenseRepo;
    private final AuthenticationServiceUtil authUtil;
    private final CurrencyRepo currencyRepo;
    private final UserRepo userRepo;
    private final CategoryRepo categoryRepo;

    public ExpenseService(ExpenseRepo expenseRepo, AuthenticationServiceUtil authUtil, CurrencyRepo currencyRepo,
            UserRepo userRepo, CategoryRepo categoryRepo) {
        this.expenseRepo = expenseRepo;
        this.authUtil = authUtil;
        this.currencyRepo = currencyRepo;
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
    }

    public Page<Expense> getAllExpenses(ExpenseFilter filter, Pageable pageable) {
        List<Specification<Expense>> specs = new ArrayList<>();
        String userId = authUtil.getCurrentUserUuid();

        specs.add((root, q, cb) -> cb.equal(root.get("user").get("id"), userId));

        if (filter.text() != null && !filter.text().isBlank()) {
            String like = "%" + filter.text().toLowerCase() + "%";
            specs.add((root, q, cb) -> {
                return cb.or(
                        cb.like(cb.lower(root.get("category").get("name")), like),
                        cb.like(cb.lower(root.get("currency").get("name")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(root.get("amount").as(String.class), like));
            });
        }
        if (filter.category() != null) {
            specs.add((root,q,cb) -> cb.equal(root.get("category").get("id"), filter.category()));
        }
        if (filter.symbol() != null) {
            specs.add((root,q,cb) -> cb.equal(root.get("currency").get("id"), filter.symbol()));
        }
        if (filter.amountStart() != null) {
            specs.add((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), filter.amountStart()));
        }
        if (filter.amountFinish() != null) {
            specs.add((root, q, cb) -> cb.lessThanOrEqualTo(root.get("amount"), filter.amountFinish()));
        }

        return expenseRepo.findAll(Specification.allOf(specs), pageable);
    }

    public Expense getExpenseById(Long id) {
        String userId = authUtil.getCurrentUserUuid();
        return expenseRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id + " for user: " + userId));
    }

    public Expense saveExpense(SaveExpenseDto expenseDto) {
        String userId = authUtil.getCurrentUserUuid();
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userId));

        Currency currency = currencyRepo.findByIdAndUserId(expenseDto.currencyId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Currency not found with id: " + expenseDto.currencyId() + " for user: " + userId));

        Category category = categoryRepo.findByIdAndUserId(expenseDto.categoryId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found with id: " + expenseDto.categoryId() + " for user: " + userId));

        Expense newExpense = new Expense(user, currency, category, expenseDto.amount(), expenseDto.description(),
                expenseDto.date());

        return expenseRepo.save(newExpense);
    }

    public Expense updateExpense(UpdateExpenseDto updateExpenseDto) {
        String userId = authUtil.getCurrentUserUuid();

        Currency currency = currencyRepo.findByIdAndUserId(updateExpenseDto.currencyId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Currency not found with id: " + updateExpenseDto.currencyId() + " for user: " + userId));

        Category category = categoryRepo.findByIdAndUserId(updateExpenseDto.categoryId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found with id: " + updateExpenseDto.categoryId() + " for user: " + userId));

        Expense oldExpense = expenseRepo.findByIdAndUserId(updateExpenseDto.id(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Expense not found with id: " + updateExpenseDto.id() + " for user: " + userId));

        oldExpense.setAmount(updateExpenseDto.amount());
        oldExpense.setCurrency(currency);
        oldExpense.setCategory(category);
        oldExpense.setDescription(updateExpenseDto.description());
        oldExpense.setDate(updateExpenseDto.date());

        return expenseRepo.save(oldExpense);
    }

    public void bulkDelete(BulkDeleteDto bulkdeleteDto) {
        String userId = authUtil.getCurrentUserUuid();

        bulkdeleteDto.ids().forEach(id -> {
            if (expenseRepo.findByIdAndUserId(id, userId).isEmpty()) {
                throw new RuntimeException("Expense not found with id: " + id + " for user: " + userId + " in bulk");
            }
        });

        expenseRepo.bulkDelete(bulkdeleteDto.ids(), userId);
    }

    public void deleteExpense(Long id) {
        String userId = authUtil.getCurrentUserUuid();

        if (expenseRepo.findByIdAndUserId(id, userId).isEmpty()) {
            throw new RuntimeException("Expense not found with id: " + id + " for user: " + userId);
        }

        expenseRepo.deleteByIdAndUserId(id, userId);
    }

}
