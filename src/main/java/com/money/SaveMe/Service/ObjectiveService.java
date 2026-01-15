package com.money.SaveMe.Service;

import com.money.SaveMe.DTO.Objective.SaveObjectiveDto;
import com.money.SaveMe.DTO.Objective.UpdateObjectiveDto;
import com.money.SaveMe.Model.Currency;
import com.money.SaveMe.Model.Income;
import com.money.SaveMe.Model.Objective;
import com.money.SaveMe.Model.User;
import com.money.SaveMe.Repo.CurrencyRepo;
import com.money.SaveMe.Repo.ObjectiveRepo;
import com.money.SaveMe.Repo.UserRepo;
import com.money.SaveMe.Utils.AuthenticationServiceUtil;
import org.springframework.stereotype.Service;

@Service
public class ObjectiveService {
    private ObjectiveRepo objectiveRepo;
    private AuthenticationServiceUtil authenticationServiceUtil;
    private UserRepo userRepo;
    private CurrencyRepo currencyRepo;

    public ObjectiveService(ObjectiveRepo objectiveRepo, AuthenticationServiceUtil authenticationServiceUtil, UserRepo userRepo, CurrencyRepo currencyRepo) {
        this.objectiveRepo = objectiveRepo;
        this.authenticationServiceUtil = authenticationServiceUtil;
        this.userRepo = userRepo;
        this.currencyRepo = currencyRepo;
    }

   public Iterable<Objective> getAllObjectives(){
        String userId = authenticationServiceUtil.getCurrentUserUuid();
        return objectiveRepo.findAllObjectiveByUserId(userId);
   }

   public Objective getObjectiveById(Long id){
        String userId = authenticationServiceUtil.getCurrentUserUuid();
        return objectiveRepo.findByObjectiveIdAndUserId(id, userId)
                .orElseThrow(()-> new IllegalArgumentException("Income not found with id: " + id + " for user: " + userId));
   }

   public Objective saveObjective(SaveObjectiveDto objective){
       String userId = authenticationServiceUtil.getCurrentUserUuid();

       User user = userRepo.findById(userId)
               .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userId));

       Currency currency = currencyRepo.findCurrencyByIdAndUserId(objective.currencyId(), userId)
               .orElseThrow(() -> new IllegalArgumentException("Currency not found with id: " + objective.currencyId() + " for user: " + userId));

       Objective newObjective = new Objective(user,currency,objective.amount(),objective.description(),objective.target());

       return objectiveRepo.save(newObjective);
   }

    public Objective updateObjective(UpdateObjectiveDto updatedObjectiveDto) {
        String userId = authenticationServiceUtil.getCurrentUserUuid();

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
    public void deleteObjective(Long id) {
        String userId = authenticationServiceUtil.getCurrentUserUuid();

        if (!objectiveRepo.findByObjectiveIdAndUserId(id, userId).isPresent()) {
            throw new RuntimeException("Objective not found with id: " + id + " for user: " + userId);
        }

        objectiveRepo.deleteObjectiveByIdAndUserId(id, userId);
    }


}
