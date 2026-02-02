package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.DTO.StrategyType.SaveStrategyTypeDto;
import com.money.SaveMi.Model.StrategyType;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.StrategyTypeRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;
import org.springframework.stereotype.Service;

@Service
public class StrategyTypeService {
    private final StrategyTypeRepo strategyTypeRepo;
    private final UserRepo userRepo;
    private final AuthenticationServiceUtil authUtil;

    public StrategyTypeService(StrategyTypeRepo strategyTypeRepo, UserRepo userRepo, AuthenticationServiceUtil authUtil) {
        this.strategyTypeRepo = strategyTypeRepo;
        this.userRepo = userRepo;
        this.authUtil = authUtil;
    }

    public Iterable<StrategyType> getAllStrategyTypes(){
        String userUUID = authUtil.getCurrentUserUuid();
        return strategyTypeRepo.findAllByUserId(userUUID);
    }

    public StrategyType getStrategyTypeById(Long id){
        String userUUID = authUtil.getCurrentUserUuid();
        return strategyTypeRepo.findStrategyTypeByIdAndUserId(id, userUUID)
                .orElseThrow(() -> new RuntimeException("StrategyType not found with id: " + id + " for user: " + userUUID));
    }

    public StrategyType saveStrategyType(SaveStrategyTypeDto strategyType) {
        String userUUID = authUtil.getCurrentUserUuid();
        User user = userRepo.findById(userUUID)
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userUUID));
        StrategyType newStrategyType = new StrategyType(user, strategyType.name(), strategyType.description());
        return strategyTypeRepo.save(newStrategyType);
    }

    public void bulkDelete(BulkDeleteDto bulkdeleteDto){
        String userId = authUtil.getCurrentUserUuid();

        bulkdeleteDto.ids().forEach(id -> {
            if(strategyTypeRepo.findStrategyTypeByIdAndUserId(id,userId).isEmpty()){
                throw new RuntimeException("Strategy type not found with id: " + id + " for user: " + userId + " in bulk");
            }
        });

        strategyTypeRepo.bulkDelete(bulkdeleteDto.ids(),userId);
    }

    public void deleteStrategyType(Long id){
        String userUUID = authUtil.getCurrentUserUuid();

        if(strategyTypeRepo.findStrategyTypeByIdAndUserId(id,userUUID).isEmpty()){
            throw new RuntimeException("Strategy type not found with id: " + id + " for user: " + userUUID);
        }

        strategyTypeRepo.deleteById(id, userUUID);
    }

    public StrategyType findStrategyTypeByNameDescriptionAndUserId(String name, String description, String userId ){
        return strategyTypeRepo.findByNameDescriptionAndUserId(name, description, userId)
                .orElse(null);

    }
}
