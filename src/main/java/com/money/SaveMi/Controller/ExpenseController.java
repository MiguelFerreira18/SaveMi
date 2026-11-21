package com.money.SaveMi.Controller;

import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.money.SaveMi.DTO.Expense.ExpenseOutDto;
import com.money.SaveMi.DTO.Expense.SaveExpenseDto;
import com.money.SaveMi.DTO.Expense.UpdateExpenseDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Filter.ExpenseFilter;
import com.money.SaveMi.Model.Expense;
import com.money.SaveMi.Service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseOutDto>> getAllExpenses(ExpenseFilter filter,
            @PageableDefault(size = 5) Pageable pageable) {
        Page<ExpenseOutDto> result = expenseService
                .getAllExpenses(filter, pageable)
                .map(expense -> new ExpenseOutDto(
                        expense.getId(),
                        expense.getCategory().getName(),
                        expense.getCurrency().getSymbol(),
                        expense.getDescription(),
                        expense.getAmount(),
                        expense.getUser().getId(),
                        expense.getDate()));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseOutDto> getExpenseById(@PathVariable Long id) {
        Expense expense = expenseService.getExpenseById(id);

        if (expense == null) {
            return ResponseEntity.notFound().build();
        }

        ExpenseOutDto expenseOutDto = new ExpenseOutDto(
                expense.getId(),
                expense.getCategory().getName(),
                expense.getCurrency().getSymbol(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getUser().getId(),
                expense.getDate());

        return ResponseEntity.ok(expenseOutDto);
    }

    @PostMapping
    public ResponseEntity<ExpenseOutDto> saveExpense(@RequestBody @Valid SaveExpenseDto expense) {
        Expense savedExpense = expenseService.saveExpense(expense);

        if (savedExpense == null) {
            return ResponseEntity.badRequest().build();
        }

        ExpenseOutDto expenseOutDto = new ExpenseOutDto(
                savedExpense.getId(),
                savedExpense.getCategory().getName(),
                savedExpense.getCurrency().getSymbol(),
                savedExpense.getDescription(),
                savedExpense.getAmount(),
                savedExpense.getUser().getId(),
                savedExpense.getDate());

        return ResponseEntity.status(201).body(expenseOutDto);
    }

    @PutMapping
    public ResponseEntity<ExpenseOutDto> updateExpense(@RequestBody @Valid UpdateExpenseDto expense) {
        Expense updatedExpense = expenseService.updateExpense(expense);

        if (updatedExpense == null) {
            return ResponseEntity.badRequest().build();
        }

        ExpenseOutDto expenseOutDto = new ExpenseOutDto(
                updatedExpense.getId(),
                updatedExpense.getCategory().getName(),
                updatedExpense.getCurrency().getSymbol(),
                updatedExpense.getDescription(),
                updatedExpense.getAmount(),
                updatedExpense.getUser().getId(),
                updatedExpense.getDate());

        return ResponseEntity.ok(expenseOutDto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteExpenses(@RequestBody @Valid BulkDeleteDto bulkDeleteDto) {
        expenseService.bulkDelete(bulkDeleteDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
