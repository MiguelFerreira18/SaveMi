package com.money.SaveMi.init;

import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Model.Wish;
import com.money.SaveMi.Repo.WishRepo;
import com.money.SaveMi.Service.CurrencyService;
import com.money.SaveMi.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Order(BootstrapOrder.WISHES)
@Profile("dev")
public class WishBootsrap implements CommandLineRunner {
    private final UserService userService;
    private final CurrencyService currencyService;
    private final WishRepo wishRepo;

    public WishBootsrap(UserService userService, CurrencyService currencyService, WishRepo wishRepo) {
        this.userService = userService;
        this.currencyService = currencyService;
        this.wishRepo = wishRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        User u1 = userService.getUserByEmail("coisa@gmail.com");
        Currency eur = currencyService.findByNameAndSymbolAndUserId("EUR", "€", u1.getId());
        Currency usd = currencyService.findByNameAndSymbolAndUserId("USD", "$", u1.getId());

        User u2 = userService.getUserByEmail("coisa2@gmail.com");
        Currency eur2 = currencyService.findByNameAndSymbolAndUserId("EUR", "€", u2.getId());

        createWishes(u1, eur, 10);
        createWishes(u1, usd, 5);
        createWishes(u2, eur2, 4);
    }

    private void createWishes(User user, Currency currency, int numberOfWishes) {
        for (int i = 1; i <= numberOfWishes; i++) {
            BigDecimal value = new BigDecimal(i * 100.23).setScale(2, BigDecimal.ROUND_HALF_UP);
            String description = String.format("WISH%d", i);
            if (wishRepo.findByWishByUserIdCurrencyIdAndAmount(user.getId(), currency.getId(), value).isEmpty()) {
                 Wish wish = new Wish(user, currency, value, description, LocalDate.now().minusDays(i));
                 wishRepo.save(wish);
            }
        }
    }
}
