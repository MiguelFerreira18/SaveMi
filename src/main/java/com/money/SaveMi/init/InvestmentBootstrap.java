package com.money.SaveMi.init;

import com.money.SaveMi.Model.*;
import com.money.SaveMi.Repo.InvestmentRepo;
import com.money.SaveMi.Service.CurrencyService;
import com.money.SaveMi.Service.StrategyTypeService;
import com.money.SaveMi.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Component
@Order(BootstrapOrder.INVESTMENTS)
@Profile("dev")
public class InvestmentBootstrap implements CommandLineRunner {
    private final UserService userService;
    private final CurrencyService currencyService;
    private final StrategyTypeService strategyTypeService;
    private final InvestmentRepo investmentRepo;

    public InvestmentBootstrap(UserService userService, CurrencyService currencyService, StrategyTypeService strategyTypeService, InvestmentRepo investmentRepo) {
        this.userService = userService;
        this.currencyService = currencyService;
        this.strategyTypeService = strategyTypeService;
        this.investmentRepo = investmentRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        User u1 = userService.getUserByEmail("coisa@gmail.com");
        StrategyType subscription = strategyTypeService.findStrategyTypeByNameDescriptionAndUserId("Aggressive", "High risk, high reward", u1.getId());
        StrategyType food = strategyTypeService.findStrategyTypeByNameDescriptionAndUserId("Conservative", "Low risk, steady growth", u1.getId());
        Currency eur = currencyService.findByNameAndSymbolAndUserId("EUR", "€", u1.getId());
        Currency usd = currencyService.findByNameAndSymbolAndUserId("USD", "$", u1.getId());

        User u2 = userService.getUserByEmail("coisa2@gmail.com");
        Currency eur2 = currencyService.findByNameAndSymbolAndUserId("EUR", "€", u2.getId());
        StrategyType entertainment = strategyTypeService.findStrategyTypeByNameDescriptionAndUserId("Savings certificates", "Low risk, fixed returns", u2.getId());

        saveInvestment(u1, eur, subscription, 5);
        saveInvestment(u1, usd, food, 3);
        saveInvestment(u2, eur2, entertainment, 4);
    }

    private void saveInvestment(User u, Currency c, StrategyType strategyType, int numberOfInvestments) {
        for(int i = 1; i <= numberOfInvestments; i++){
            BigDecimal value = new BigDecimal( i * 100.23).setScale(2, RoundingMode.HALF_UP);
            final String description = String.format("INVESTMENT%d", i);
            if (investmentRepo.findInvestmentByAllparameters(u.getId(),strategyType.getId(),c.getId(),value).isEmpty()) {
                Investment expense = new Investment(u, c, value,  strategyType,description, LocalDate.now().minusDays(i));
                investmentRepo.save(expense);
            }
        }
    }
}
