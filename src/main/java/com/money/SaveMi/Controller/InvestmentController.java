package com.money.SaveMi.Controller;

import com.money.SaveMi.DTO.Investment.InvestmentOutDto;
import com.money.SaveMi.DTO.Investment.SaveInvestmentDto;
import com.money.SaveMi.DTO.Investment.UpdateInvestmentDto;
import com.money.SaveMi.Model.Investment;
import com.money.SaveMi.Service.InvestmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/investment")
public class InvestmentController {
    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<InvestmentOutDto>> getAllInvestments() {
        Iterable<Investment> investments = investmentService.getAllInvestments();

        if (investments == null) {
            return ResponseEntity.notFound().build();
        }

        Iterable<InvestmentOutDto> InvestmentOutDtos = StreamSupport.stream(investments.spliterator(),false).map(
                investment -> new InvestmentOutDto(
                        investment.getId(),
                        investment.getStrategyType().getName(),
                        investment.getCurrency().getSymbol(),
                        investment.getDescription(),
                        investment.getAmount(),
                        investment.getUser().getId(),
                        investment.getDate()
                )
        ).toList();

        return ResponseEntity.ok(InvestmentOutDtos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<InvestmentOutDto> getInvestmentById(@PathVariable Long id) {
        Investment investment = investmentService.getInvestmentById(id);

        if (investment == null) {
            return ResponseEntity.notFound().build();
        }

        InvestmentOutDto investmentOutDto = new InvestmentOutDto(
                investment.getId(),
                investment.getStrategyType().getName(),
                investment.getCurrency().getSymbol(),
                investment.getDescription(),
                investment.getAmount(),
                investment.getUser().getId(),
                investment.getDate()
        );

        return ResponseEntity.ok(investmentOutDto);
    }


    @PostMapping
    public ResponseEntity<InvestmentOutDto> saveExpense(@RequestBody SaveInvestmentDto investment) {
        Investment savedInvestment = investmentService.saveInvestment(investment);

        if (savedInvestment == null) {
            return ResponseEntity.badRequest().build();
        }

        InvestmentOutDto investmentOutDto = new InvestmentOutDto (
                savedInvestment.getId(),
                savedInvestment.getStrategyType().getName(),
                savedInvestment.getCurrency().getSymbol(),
                savedInvestment.getDescription(),
                savedInvestment.getAmount(),
                savedInvestment.getUser().getId(),
                savedInvestment.getDate()
        );

        return ResponseEntity.status(201).body(investmentOutDto);
    }

    @PutMapping
    public ResponseEntity<InvestmentOutDto> updateExpense(@RequestBody UpdateInvestmentDto investment) {
        Investment updateInvestment = investmentService.updateInvestment(investment);

        if (updateInvestment == null) {
            return ResponseEntity.badRequest().build();
        }

        InvestmentOutDto investmentOutDto = new InvestmentOutDto(
                updateInvestment.getId(),
                updateInvestment.getStrategyType().getName(),
                updateInvestment.getCurrency().getSymbol(),
                updateInvestment.getDescription(),
                updateInvestment.getAmount(),
                updateInvestment.getUser().getId(),
                updateInvestment.getDate()
        );

        return ResponseEntity.ok(investmentOutDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvestment(@PathVariable Long id) {
        investmentService.deleteInvestment(id);
        return ResponseEntity.status(202).build();
    }

}
