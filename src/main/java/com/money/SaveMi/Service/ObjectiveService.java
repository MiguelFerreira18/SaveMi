package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Objective.SaveObjectiveDto;
import com.money.SaveMi.DTO.Objective.UpdateObjectiveDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Filter.ObjectiveFilter;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Objective;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.ObjectiveRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ObjectiveService {
    private ObjectiveRepo objectiveRepo;
    private AuthenticationServiceUtil authUtil;
    private UserRepo userRepo;
    private CurrencyRepo currencyRepo;

    public ObjectiveService(ObjectiveRepo objectiveRepo, AuthenticationServiceUtil authUtil, UserRepo userRepo,
            CurrencyRepo currencyRepo) {
        this.objectiveRepo = objectiveRepo;
        this.authUtil = authUtil;
        this.userRepo = userRepo;
        this.currencyRepo = currencyRepo;
    }

    public Page<Objective> getAllObjectives(ObjectiveFilter filter, Pageable pageable) {
        List<Specification<Objective>> specs = new ArrayList<>();
        String userId = authUtil.getCurrentUserUuid();
        specs.add((root, q, cb) -> cb.equal(root.get("user").get("id"), userId));
        
        if (filter.text() != null && !filter.text().isBlank()) {
            String like = "%" + filter.text().toLowerCase() + "%";
            specs.add((root, q, cb) -> {
                return cb.or(
                        cb.like(cb.lower(root.get("currency").get("name")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(root.get("amount").as(String.class), like));
            });
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
        if (filter.targetStart() != null) {
            specs.add((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("target"), filter.targetStart()));
        }
        if (filter.targetFinish() != null) {
            specs.add((root, q, cb) -> cb.lessThanOrEqualTo(root.get("target"), filter.targetFinish()));
        }
        return objectiveRepo.findAll(Specification.allOf(specs), pageable);
    }

    public Objective getObjectiveById(Long id) {
        String userId = authUtil.getCurrentUserUuid();
        return objectiveRepo.findByIdAndUserId(id, userId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Income not found with id: " + id + " for user: " + userId));
    }

    public Objective saveObjective(SaveObjectiveDto objective) {
        String userId = authUtil.getCurrentUserUuid();

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userId));

        Currency currency = currencyRepo.findByIdAndUserId(objective.currencyId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Currency not found with id: " + objective.currencyId() + " for user: " + userId));

        Objective newObjective = new Objective(user, currency, objective.amount(), objective.description(),
                objective.target());

        return objectiveRepo.save(newObjective);
    }

    public Objective updateObjective(UpdateObjectiveDto updatedObjectiveDto) {
        String userId = authUtil.getCurrentUserUuid();

        Currency currency = currencyRepo.findByIdAndUserId(updatedObjectiveDto.currencyId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Currency not found with id: " + updatedObjectiveDto.currencyId() + " for user: " + userId));

        Objective oldObjective = objectiveRepo.findByIdAndUserId(updatedObjectiveDto.id(), userId)
                .orElseThrow(() -> new RuntimeException(
                        "Objective not found with id: " + updatedObjectiveDto.id() + " for user: " + userId));
        oldObjective.setAmount(updatedObjectiveDto.amount());
        oldObjective.setDescription(updatedObjectiveDto.description());
        oldObjective.setCurrency(currency);
        oldObjective.setTarget(updatedObjectiveDto.target());

        return objectiveRepo.save(oldObjective);
    }

    public void bulkDelete(BulkDeleteDto bulkdeleteDto) {
        String userId = authUtil.getCurrentUserUuid();

        bulkdeleteDto.ids().forEach(id -> {
            if (objectiveRepo.findByIdAndUserId(id, userId).isEmpty()) {
                throw new RuntimeException("Objective not found with id: " + id + " for user: " + userId + " in bulk");
            }
        });

        objectiveRepo.bulkDelete(bulkdeleteDto.ids(), userId);
    }

    public void deleteObjective(Long id) {
        String userId = authUtil.getCurrentUserUuid();

        if (objectiveRepo.findByIdAndUserId(id, userId).isEmpty()) {
            throw new RuntimeException("Objective not found with id: " + id + " for user: " + userId);
        }

        objectiveRepo.deleteByIdAndUserId(id, userId);
    }

}
