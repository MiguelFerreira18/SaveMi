package com.money.SaveMi.Controller;

import com.money.SaveMi.DTO.Expense.ExpenseOutDto;
import com.money.SaveMi.DTO.Expense.SaveExpenseDto;
import com.money.SaveMi.DTO.Expense.UpdateExpenseDto;
import com.money.SaveMi.Model.Expense;
import com.money.SaveMi.Service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<ExpenseOutDto>> getAllExpenses() {
        Iterable<Expense> expenses = expenseService.getAllExpenses();

        if (expenses == null) {
            return ResponseEntity.notFound().build();
        }

        Iterable<ExpenseOutDto> expenseOutDtos = StreamSupport.stream(expenses.spliterator(),false).map(
                expense -> new ExpenseOutDto(
                        expense.getId(),
                        expense.getCategory().getName(),
                        expense.getCurrency().getSymbol(),
                        expense.getDescription(),
                        expense.getAmount(),
                        expense.getUser().getId(),
                        expense.getDate()
                )
        ).toList();

        return ResponseEntity.ok(expenseOutDtos);
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
                expense.getDate()
        );

        return ResponseEntity.ok(expenseOutDto);
    }


    @PostMapping
    public ResponseEntity<ExpenseOutDto> saveExpense(@RequestBody SaveExpenseDto expense) {
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
                savedExpense.getDate()
        );

        return ResponseEntity.status(201).body(expenseOutDto);
    }

    @PutMapping
    public ResponseEntity<ExpenseOutDto> updateExpense(@RequestBody UpdateExpenseDto expense) {
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
                updatedExpense.getDate()
        );

        return ResponseEntity.ok(expenseOutDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.status(202).build();
    }
}
