package com.money.SaveMi.init;

import com.money.SaveMi.DTO.StrategyType.SaveStrategyTypeDto;
import com.money.SaveMi.Model.StrategyType;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.StrategyTypeRepo;
import com.money.SaveMi.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(BootstrapOrder.STRATEGY_TYPES)
@Profile("dev")
public class StrategyTypeBootstrap implements CommandLineRunner {
    private final UserService userService;
    private final StrategyTypeRepo strategyTypeRepo;

    public StrategyTypeBootstrap(UserService userService, StrategyTypeRepo strategyTypeRepo) {
        this.userService = userService;
        this.strategyTypeRepo = strategyTypeRepo;
    }


    @Override
    public void run(String... args) throws Exception {
        User u1 = userService.getUserByEmail("coisa@gmail.com");
        User u2 = userService.getUserByEmail("coisa2@gmail.com");

        SaveStrategyTypeDto strategy1 = new SaveStrategyTypeDto("Aggressive", "High risk, high reward");
        SaveStrategyTypeDto strategy2 = new SaveStrategyTypeDto("Conservative", "Low risk, steady growth");
        SaveStrategyTypeDto strategy3 = new SaveStrategyTypeDto("Balanced", "Mix of risk and reward");
        SaveStrategyTypeDto strategy4 = new SaveStrategyTypeDto("Savings certificates", "Low risk, fixed returns");

        saveStrategyType(u1, strategy1);
        saveStrategyType(u1, strategy2);
        saveStrategyType(u1, strategy3);
        saveStrategyType(u2, strategy1);
        saveStrategyType(u2, strategy2);
        saveStrategyType(u2, strategy4);

    }


    private void saveStrategyType(User u, SaveStrategyTypeDto strategyTypeDto) {
        if (strategyTypeRepo.findByNameDescriptionAndUserId(strategyTypeDto.name(),strategyTypeDto.description(), u.getId()).isEmpty()) {
            strategyTypeRepo.save(new StrategyType(u, strategyTypeDto.name(), strategyTypeDto.description()));
        }

    }
}
