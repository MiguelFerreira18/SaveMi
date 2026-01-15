package com.money.SaveMe.init;

import com.money.SaveMe.Model.Currency;
import com.money.SaveMe.Model.Income;
import com.money.SaveMe.Model.User;
import com.money.SaveMe.Repo.IncomeRepo;
import com.money.SaveMe.Service.CurrencyService;
import com.money.SaveMe.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Component
@Order(BootstrapOrder.INCOMES)
@Profile("dev")
public class IncomeBootstrap implements CommandLineRunner {
    private final UserService userService;
    private final CurrencyService currencyService;
    private final IncomeRepo incomeRepo;


    public IncomeBootstrap(UserService userService, CurrencyService currencyService, IncomeRepo incomeRepo) {
        this.userService = userService;
        this.currencyService = currencyService;
        this.incomeRepo = incomeRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        User u1 = userService.getUserByEmail("coisa@gmail.com");
        Currency eur = currencyService.findByNameAndSymbolAndUserId("EUR", "€", u1.getId());
        Currency usd = currencyService.findByNameAndSymbolAndUserId("USD", "$", u1.getId());
        int numberOfIncomesEUR = 40;
        int numberOfIncomesDOLLAR = 5;
        createIncomes(u1, eur, numberOfIncomesEUR);
        createIncomes(u1, usd, numberOfIncomesDOLLAR);

        User u2 = userService.getUserByEmail("coisa2@gmail.com");
        Currency eur2 = currencyService.findByNameAndSymbolAndUserId("EUR", "€", u2.getId());
        int numberOfIncomesEUR2 = 4;
        createIncomes(u2, eur2, numberOfIncomesEUR2);

    }

    private void createIncomes(User user, Currency currency, int numberOfIncomes) {
        for (int i = 1; i <= numberOfIncomes; i++) {
            BigDecimal value = new BigDecimal(i * 100.23).setScale(2, RoundingMode.HALF_UP);
            String description = String.format("INCOME%d",i);
            if (incomeRepo.findIncomeByUserIdCurrencyAndAmount(user.getId(), currency.getId(), value).isEmpty()) {
                Income income = new Income(user, currency, value, description, LocalDate.now().minusDays(i));
                incomeRepo.save(income);
            }

        }
    }
}
