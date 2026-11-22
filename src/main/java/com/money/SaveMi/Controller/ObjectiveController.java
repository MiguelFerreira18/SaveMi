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

import com.money.SaveMi.DTO.Objective.ObjectiveOutputDto;
import com.money.SaveMi.DTO.Objective.SaveObjectiveDto;
import com.money.SaveMi.DTO.Objective.UpdateObjectiveDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Filter.ObjectiveFilter;
import com.money.SaveMi.Model.Objective;
import com.money.SaveMi.Service.ObjectiveService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/objectives")
public class ObjectiveController {
    private ObjectiveService objectiveService;

    public ObjectiveController(ObjectiveService objectiveService) {
        this.objectiveService = objectiveService;
    }

    @GetMapping
    @Transactional
    public ResponseEntity<Page<ObjectiveOutputDto>> getAllObjectiveByUserIdFromCurrency(ObjectiveFilter filter,
            @PageableDefault(size = 5) Pageable pageable) {
        Page<ObjectiveOutputDto> result = objectiveService
                .getAllObjectives(filter, pageable)
                .map(
                        objective -> new ObjectiveOutputDto(
                                objective.getId(),
                                objective.getCurrency().getSymbol(),
                                objective.getDescription(),
                                objective.getAmount(),
                                objective.getUser().getId(),
                                objective.getTarget()));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjectiveOutputDto> getObjectiveById(@PathVariable Long id) {
        Objective objective = objectiveService.getObjectiveById(id);

        if (objective == null) {
            return ResponseEntity.notFound().build();
        }

        ObjectiveOutputDto objectiveOutputDto = new ObjectiveOutputDto(
                objective.getId(),
                objective.getCurrency().getSymbol(),
                objective.getDescription(),
                objective.getAmount(),
                objective.getUser().getId(),
                objective.getTarget()

        );

        return ResponseEntity.ok(objectiveOutputDto);
    }

    @PostMapping
    public ResponseEntity<ObjectiveOutputDto> saveObjective(@RequestBody @Valid SaveObjectiveDto objective) {
        Objective saveObjective = objectiveService.saveObjective(objective);

        if (saveObjective == null) {
            return ResponseEntity.badRequest().build();
        }

        ObjectiveOutputDto objectiveOutputDto = new ObjectiveOutputDto(
                saveObjective.getId(),
                saveObjective.getCurrency().getSymbol(),
                saveObjective.getDescription(),
                saveObjective.getAmount(),
                saveObjective.getUser().getId(),
                saveObjective.getTarget());

        return ResponseEntity.ok(objectiveOutputDto);
    }

    @PutMapping
    public ResponseEntity<ObjectiveOutputDto> updateObjective(@RequestBody @Valid UpdateObjectiveDto objective) {
        Objective updateObjective = objectiveService.updateObjective(objective);

        if (updateObjective == null) {
            return ResponseEntity.badRequest().build();
        }

        ObjectiveOutputDto incomeOutputDto = new ObjectiveOutputDto(
                updateObjective.getId(),
                updateObjective.getCurrency().getSymbol(),
                updateObjective.getDescription(),
                updateObjective.getAmount(),
                updateObjective.getUser().getId(),
                updateObjective.getTarget());

        return ResponseEntity.ok(incomeOutputDto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteObjectives(@RequestBody @Valid BulkDeleteDto bulkDeleteDto) {
        objectiveService.bulkDelete(bulkDeleteDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObjective(@PathVariable Long id) {
        objectiveService.deleteObjective(id);
        return ResponseEntity.status(202).build();
    }
}
