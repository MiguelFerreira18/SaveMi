package com.money.SaveMi.Controller;

import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.DTO.StrategyType.SaveStrategyTypeDto;
import com.money.SaveMi.DTO.StrategyType.StrategyTypeOutputDto;
import com.money.SaveMi.Model.StrategyType;
import com.money.SaveMi.Service.StrategyTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/strategy-type")
public class StrategyTypeController {
    private final StrategyTypeService strategyTypeService;

    public StrategyTypeController(StrategyTypeService strategyTypeService) {
        this.strategyTypeService = strategyTypeService;
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<StrategyTypeOutputDto>> GetAllStrategyTypes() {
        Iterable<StrategyType> strategyTypes = strategyTypeService.getAllStrategyTypes();

        if (strategyTypes == null) {
            return ResponseEntity.notFound().build();
        }

        Iterable<StrategyTypeOutputDto> strategyTypeOutputDtos = StreamSupport.stream(strategyTypes.spliterator(), false).map(
                strategyType -> new StrategyTypeOutputDto(
                        strategyType.getId(),
                        strategyType.getName(),
                        strategyType.getDescription()
                )
        ).toList();

        return ResponseEntity.ok(strategyTypeOutputDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StrategyTypeOutputDto> GetStrategyTypeById(Long id) {
        StrategyType strategyType = strategyTypeService.getStrategyTypeById(id);

        if (strategyType == null) {
            return ResponseEntity.notFound().build();
        }

        StrategyTypeOutputDto strategyTypeOutputDto = new StrategyTypeOutputDto(
                strategyType.getId(),
                strategyType.getName(),
                strategyType.getDescription()
        );

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
                strategyType.getDescription()
        );

        return ResponseEntity.ok(strategyTypeOutputDto);
    }

    @PostMapping("/bulk-delete")
    public ResponseEntity<Void> deleteStrategyTypes(@RequestBody BulkDeleteDto bulkDeleteDto){
        strategyTypeService.bulkDelete(bulkDeleteDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> DeleteStrategyType(@PathVariable Long id) {
        strategyTypeService.deleteStrategyType(id);
        return ResponseEntity.status(202).build();
    }

}
