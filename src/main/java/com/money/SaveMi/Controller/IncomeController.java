package com.money.SaveMi.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.money.SaveMi.DTO.Income.IncomeOutputDto;
import com.money.SaveMi.DTO.Income.SaveIncomeDto;
import com.money.SaveMi.DTO.Income.UpdateIncomeDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Filter.IncomeFilter;
import com.money.SaveMi.Model.Income;
import com.money.SaveMi.Service.IncomeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {
    private IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @GetMapping
    @Transactional
    public ResponseEntity<Page<IncomeOutputDto>> getIncomesByUserIdFromCurrency(IncomeFilter filter,
            @PageableDefault(size = 5) Pageable pageable) {
        Page<IncomeOutputDto> result = incomeService
                .getAllIncomeByUserId(filter, pageable)
                .map(income -> new IncomeOutputDto(
                        income.getId(),
                        income.getCurrency().getSymbol(),
                        income.getDescription(),
                        income.getAmount(),
                        income.getUser().getId(),
                        income.getDate()));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeOutputDto> getIncomeById(@PathVariable Long id) {
        Income income = incomeService.getIncomeById(id);

        if (income == null) {
            return ResponseEntity.notFound().build();
        }

        IncomeOutputDto incomeOutputDto = new IncomeOutputDto(
                income.getId(),
                income.getCurrency().getSymbol(),
                income.getDescription(),
                income.getAmount(),
                income.getUser().getId(),
                income.getDate()

        );

        return ResponseEntity.ok(incomeOutputDto);
    }

    @PostMapping
    public ResponseEntity<IncomeOutputDto> saveIncome(@RequestBody @Valid SaveIncomeDto income) {
        Income savedIncome = incomeService.saveIncome(income);

        if (savedIncome == null) {
            return ResponseEntity.badRequest().build();
        }

        IncomeOutputDto incomeOutputDto = new IncomeOutputDto(
                savedIncome.getId(),
                savedIncome.getCurrency().getSymbol(),
                savedIncome.getDescription(),
                savedIncome.getAmount(),
                savedIncome.getUser().getId(),
                savedIncome.getDate());

        return ResponseEntity.ok(incomeOutputDto);
    }

    @PutMapping
    public ResponseEntity<IncomeOutputDto> updateIncome(@RequestBody @Valid UpdateIncomeDto income) {
        Income updatedIncome = incomeService.updateIncome(income);

        if (updatedIncome == null) {
            return ResponseEntity.badRequest().build();
        }

        IncomeOutputDto incomeOutputDto = new IncomeOutputDto(
                updatedIncome.getId(),
                updatedIncome.getCurrency().getSymbol(),
                updatedIncome.getDescription(),
                updatedIncome.getAmount(),
                updatedIncome.getUser().getId(),
                updatedIncome.getDate());

        return ResponseEntity.ok(incomeOutputDto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteIncomes(@RequestBody @Valid BulkDeleteDto bulkDeleteDto) {
        incomeService.bulkDelete(bulkDeleteDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.status(202).build();
    }
}
