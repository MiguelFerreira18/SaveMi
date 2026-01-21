package com.money.SaveMi.Service;

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
    private final AuthenticationServiceUtil authenticationServiceUtil;

    public StrategyTypeService(StrategyTypeRepo strategyTypeRepo, UserRepo userRepo, AuthenticationServiceUtil authenticationServiceUtil) {
        this.strategyTypeRepo = strategyTypeRepo;
        this.userRepo = userRepo;
        this.authenticationServiceUtil = authenticationServiceUtil;
    }

    public Iterable<StrategyType> getAllStrategyTypes(){
        String userUUID = authenticationServiceUtil.getCurrentUserUuid();
        return strategyTypeRepo.findAllByUserId(userUUID);
    }

    public StrategyType getStrategyTypeById(Long id){
        String userUUID = authenticationServiceUtil.getCurrentUserUuid();
        return strategyTypeRepo.findStrategyTypeByIdAndUserId(id, userUUID)
                .orElseThrow(() -> new RuntimeException("StrategyType not found with id: " + id + " for user: " + userUUID));
    }

    public StrategyType saveStrategyType(SaveStrategyTypeDto strategyType) {
        String userUUID = authenticationServiceUtil.getCurrentUserUuid();
        User user = userRepo.findById(userUUID)
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userUUID));
        StrategyType newStrategyType = new StrategyType(user, strategyType.name(), strategyType.description());
        return strategyTypeRepo.save(newStrategyType);
    }

    public void deleteStrategyType(Long id){
        String userUUID = authenticationServiceUtil.getCurrentUserUuid();
        strategyTypeRepo.deleteById(id, userUUID);
    }

    public StrategyType findStrategyTypeByNameDescriptionAndUserId(String name, String description, String userId ){
        return strategyTypeRepo.findByNameDescriptionAndUserId(name, description, userId)
                .orElse(null);

    }
}
