package com.money.SaveMi.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.DTO.StrategyType.SaveStrategyTypeDto;
import com.money.SaveMi.DTO.StrategyType.StrategyTypeOutputDto;
import com.money.SaveMi.Model.StrategyType;
import com.money.SaveMi.Service.StrategyTypeService;

@RestController
@RequestMapping("/api/strategies")
public class StrategyTypeController {
    private final StrategyTypeService strategyTypeService;

    public StrategyTypeController(StrategyTypeService strategyTypeService) {
        this.strategyTypeService = strategyTypeService;
    }

    @GetMapping
    public ResponseEntity<Page<StrategyTypeOutputDto>> GetAllStrategyTypes(@PageableDefault(5) Pageable pageable) {
        Page<StrategyTypeOutputDto> result = strategyTypeService
                .getAllStrategyTypes(pageable)
                .map(
                        strategyType -> new StrategyTypeOutputDto(
                                strategyType.getId(),
                                strategyType.getName(),
                                strategyType.getDescription()));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StrategyTypeOutputDto> GetStrategyTypeById(@PathVariable Long id) {
        StrategyType strategyType = strategyTypeService.getStrategyTypeById(id);

        if (strategyType == null) {
            return ResponseEntity.notFound().build();
        }

        StrategyTypeOutputDto strategyTypeOutputDto = new StrategyTypeOutputDto(
                strategyType.getId(),
                strategyType.getName(),
                strategyType.getDescription());

        return ResponseEntity.ok(strategyTypeOutputDto);
    }

    @PostMapping
    public ResponseEntity<StrategyTypeOutputDto> SaveStrategyType(@RequestBody SaveStrategyTypeDto strategyTypeDto) {
        StrategyType strategyType = strategyTypeService.saveStrategyType(strategyTypeDto);

        if (strategyType == null) {
            return ResponseEntity.badRequest().build();
        }

        StrategyTypeOutputDto strategyTypeOutputDto = new StrategyTypeOutputDto(
                strategyType.getId(),
                strategyType.getName(),
                strategyType.getDescription());

        return ResponseEntity.ok(strategyTypeOutputDto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteStrategyTypes(@RequestBody BulkDeleteDto bulkDeleteDto) {
        strategyTypeService.bulkDelete(bulkDeleteDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> DeleteStrategyType(@PathVariable Long id) {
        strategyTypeService.deleteStrategyType(id);
        return ResponseEntity.noContent().build();
    }

}
