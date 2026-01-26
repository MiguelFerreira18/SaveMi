package com.money.SaveMi.Controller;

import com.money.SaveMi.DTO.Income.IncomeOutputDto;
import com.money.SaveMi.DTO.Income.SaveIncomeDto;
import com.money.SaveMi.DTO.Income.UpdateIncomeDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Income;
import com.money.SaveMi.Service.IncomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/income")
public class IncomeController {
    private IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @GetMapping("/all")
    @Transactional
    public ResponseEntity<Iterable<IncomeOutputDto>> getAllIncomeByUserIdFromCurrency() {
        Iterable<Income> incomes = incomeService.getAllIncomeByUserId();

        if (incomes == null) {
            return ResponseEntity.notFound().build();
        }

        Iterable<IncomeOutputDto> incomeOutputDtos = StreamSupport.stream(incomes.spliterator(), false).map(
                income -> new IncomeOutputDto(
                        income.getId(),
                        income.getCurrency().getSymbol(),
                        income.getDescription(),
                        income.getAmount(),
                        income.getUser().getId(),
                        income.getDate()
                )
        ).toList();

        return ResponseEntity.ok(incomeOutputDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeOutputDto> getIncomeById(@PathVariable Long incomeId) {
        Income income = incomeService.getIncomeById(incomeId);

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
    public ResponseEntity<IncomeOutputDto> saveIncome(@RequestBody SaveIncomeDto income) {
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
                savedIncome.getDate()
        );

        return ResponseEntity.ok(incomeOutputDto);
    }

    @PutMapping
    public ResponseEntity<IncomeOutputDto> updateIncome(@RequestBody UpdateIncomeDto income) {
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
                updatedIncome.getDate()
        );

        return ResponseEntity.ok(incomeOutputDto);
    }

    @PostMapping("/bulk-delete")
    public ResponseEntity<Void> deleteIncomes(@RequestBody BulkDeleteDto bulkDeleteDto){
        incomeService.bulkDelete(bulkDeleteDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.status(202).build();
    }

    private LocalDateTime parserToLocalDateTime(Instant instant) {
        ZoneId zoneId = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zoneId);

    }


}
