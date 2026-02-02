package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Objective.SaveObjectiveDto;
import com.money.SaveMi.DTO.Objective.UpdateObjectiveDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Objective;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.ObjectiveRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;
import org.springframework.stereotype.Service;

@Service
public class ObjectiveService {
    private ObjectiveRepo objectiveRepo;
    private AuthenticationServiceUtil authUtil;
    private UserRepo userRepo;
    private CurrencyRepo currencyRepo;

    public ObjectiveService(ObjectiveRepo objectiveRepo, AuthenticationServiceUtil authUtil, UserRepo userRepo, CurrencyRepo currencyRepo) {
        this.objectiveRepo = objectiveRepo;
        this.authUtil = authUtil;
        this.userRepo = userRepo;
        this.currencyRepo = currencyRepo;
    }

   public Iterable<Objective> getAllObjectives(){
        String userId = authUtil.getCurrentUserUuid();
        return objectiveRepo.findAllObjectiveByUserId(userId);
   }

   public Objective getObjectiveById(Long id){
        String userId = authUtil.getCurrentUserUuid();
        return objectiveRepo.findByObjectiveIdAndUserId(id, userId)
                .orElseThrow(()-> new IllegalArgumentException("Income not found with id: " + id + " for user: " + userId));
   }

   public Objective saveObjective(SaveObjectiveDto objective){
       String userId = authUtil.getCurrentUserUuid();

       User user = userRepo.findById(userId)
               .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userId));

       Currency currency = currencyRepo.findCurrencyByIdAndUserId(objective.currencyId(), userId)
               .orElseThrow(() -> new IllegalArgumentException("Currency not found with id: " + objective.currencyId() + " for user: " + userId));

       Objective newObjective = new Objective(user,currency,objective.amount(),objective.description(),objective.target());

       return objectiveRepo.save(newObjective);
   }

    public Objective updateObjective(UpdateObjectiveDto updatedObjectiveDto) {
        String userId = authUtil.getCurrentUserUuid();

        Currency currency = currencyRepo.findCurrencyByIdAndUserId(updatedObjectiveDto.currencyId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Currency not found with id: " + updatedObjectiveDto.currencyId() + " for user: " + userId));

        Objective oldObjective = objectiveRepo.findByObjectiveIdAndUserId(updatedObjectiveDto.id(), userId)
                .orElseThrow(() -> new RuntimeException("Objective not found with id: " + updatedObjectiveDto.id() + " for user: " + userId));
        oldObjective.setAmount(updatedObjectiveDto.amount());
        oldObjective.setDescription(updatedObjectiveDto.description());
        oldObjective.setCurrency(currency);
        oldObjective.setTarget(updatedObjectiveDto.target());

        return objectiveRepo.save(oldObjective);
    }

    public void bulkDelete(BulkDeleteDto bulkdeleteDto){
        String userId = authUtil.getCurrentUserUuid();

        bulkdeleteDto.ids().forEach(id -> {
            if(objectiveRepo.findByObjectiveIdAndUserId(id,userId).isEmpty()){
                throw new RuntimeException("Objective not found with id: " + id + " for user: " + userId + " in bulk");
            }
        });

        objectiveRepo.bulkDelete(bulkdeleteDto.ids(),userId);
    }

    public void deleteObjective(Long id) {
        String userId = authUtil.getCurrentUserUuid();

        if (objectiveRepo.findByObjectiveIdAndUserId(id, userId).isEmpty()) {
            throw new RuntimeException("Objective not found with id: " + id + " for user: " + userId);
        }

        objectiveRepo.deleteObjectiveByIdAndUserId(id, userId);
    }


}
