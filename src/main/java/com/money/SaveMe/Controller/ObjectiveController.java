package com.money.SaveMe.Controller;

import com.money.SaveMe.DTO.Income.IncomeOutputDto;
import com.money.SaveMe.DTO.Income.SaveIncomeDto;
import com.money.SaveMe.DTO.Income.UpdateIncomeDto;
import com.money.SaveMe.DTO.Objective.ObjectiveOutputDto;
import com.money.SaveMe.DTO.Objective.SaveObjectiveDto;
import com.money.SaveMe.DTO.Objective.UpdateObjectiveDto;
import com.money.SaveMe.Model.Income;
import com.money.SaveMe.Model.Objective;
import com.money.SaveMe.Service.IncomeService;
import com.money.SaveMe.Service.ObjectiveService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.ObjectView;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/objective")
public class ObjectiveController {
    private ObjectiveService objectiveService;

    public ObjectiveController(ObjectiveService objectiveService) {
        this.objectiveService = objectiveService;
    }

    @GetMapping("/all")
    @Transactional
    public ResponseEntity<Iterable<ObjectiveOutputDto>> getAllObjectiveByUserIdFromCurrency() {
        Iterable<Objective> objectives = objectiveService.getAllObjectives();

        if (objectives == null) {
            return ResponseEntity.notFound().build();
        }

        Iterable<ObjectiveOutputDto> objectiveOutputDtos = StreamSupport.stream(objectives.spliterator(), false).map(
                objective -> new ObjectiveOutputDto(
                        objective.getId(),
                        objective.getCurrency().getSymbol(),
                        objective.getDescription(),
                        objective.getAmount(),
                        objective.getUser().getId(),
                        objective.getTarget()
                )
        ).toList();

        return ResponseEntity.ok(objectiveOutputDtos);
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
    public ResponseEntity<ObjectiveOutputDto> saveObjective(@RequestBody SaveObjectiveDto objective) {
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
                saveObjective.getTarget()
        );

        return ResponseEntity.ok(objectiveOutputDto);
    }

    @PutMapping
    public ResponseEntity<ObjectiveOutputDto> updateObjective(@RequestBody UpdateObjectiveDto objective) {
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
                updateObjective.getTarget()
        );

        return ResponseEntity.ok(incomeOutputDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObjective(@PathVariable Long id) {
        objectiveService.deleteObjective(id);
        return ResponseEntity.status(202).build();
    }
}
