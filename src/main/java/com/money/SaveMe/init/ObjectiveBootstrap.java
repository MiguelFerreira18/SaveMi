package com.money.SaveMe.init;

import com.money.SaveMe.Model.Currency;
import com.money.SaveMe.Model.Income;
import com.money.SaveMe.Model.Objective;
import com.money.SaveMe.Model.User;
import com.money.SaveMe.Repo.ObjectiveRepo;
import com.money.SaveMe.Service.CurrencyService;
import com.money.SaveMe.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Random;

@Component
@Order(BootstrapOrder.INCOMES)
@Profile("dev")
public class ObjectiveBootstrap implements CommandLineRunner {
    private final UserService userService;
    private final CurrencyService currencyService;
    private final ObjectiveRepo objectiveRepo;


    public ObjectiveBootstrap(UserService userService, CurrencyService currencyService, ObjectiveRepo objectiveRepo) {
        this.userService = userService;
        this.currencyService = currencyService;
        this.objectiveRepo = objectiveRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        User u1 = userService.getUserByEmail("coisa@gmail.com");
        Currency eur = currencyService.findByNameAndSymbolAndUserId("EUR", "€", u1.getId());
        Currency usd = currencyService.findByNameAndSymbolAndUserId("USD", "$", u1.getId());
        int numberOfObjectivesEUR = 40;
        int numberOfObjectivesDOLLAR = 5;
        createObjectives(u1, eur, numberOfObjectivesEUR);
        createObjectives(u1, usd, numberOfObjectivesDOLLAR);

        User u2 = userService.getUserByEmail("coisa2@gmail.com");
        Currency eur2 = currencyService.findByNameAndSymbolAndUserId("EUR", "€", u2.getId());
        int numberOfObjectivesEUR2 = 4;
        createObjectives(u2, eur2, numberOfObjectivesEUR2);

    }

    private void createObjectives(User user, Currency currency, int numberOfObjectives) {
        for (int i = 1; i <= numberOfObjectives; i++) {
            BigDecimal value = new BigDecimal(i * 100.23).setScale(2, RoundingMode.HALF_UP);
            String description = String.format("OBJECTIVE%d", i);
            int year = randomYear();
            if (objectiveRepo.findObjectiveByAllFields(user.getId(), currency.getId(), value,year).isEmpty()) {
                Objective objective = new Objective(user, currency, value, description, year);
                objectiveRepo.save(objective);
            }

        }
    }

    private int randomYear(){
        Random random = new Random();
        return random.nextInt(2030 - 2025 + 1) + 2025;
    }
}
