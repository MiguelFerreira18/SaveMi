package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Currency.SaveCurrencyDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {
    private final CurrencyRepo currencyRepo;
    private final UserRepo userRepo;
    private final AuthenticationServiceUtil authenticationServiceUtil;

    public CurrencyService(CurrencyRepo currencyRepo, UserRepo userRepo, AuthenticationServiceUtil authenticationServiceUtil) {
        this.currencyRepo = currencyRepo;
        this.userRepo = userRepo;
        this.authenticationServiceUtil = authenticationServiceUtil;
    }

    public Iterable<Currency> getAllCurrenciesFromUser() {
        String userUUID = authenticationServiceUtil.getCurrentUserUuid();
        return currencyRepo.findAllCurrenciesByUserId(userUUID);
    }

    public Currency getCurrencyById(Long id) {
        String userUUID = authenticationServiceUtil.getCurrentUserUuid();
        return currencyRepo.findCurrencyByIdAndUserId(id, userUUID)
                .orElseThrow(() -> new RuntimeException("Currency not found with id: " + id + " for user: " + userUUID));
    }

    public Currency saveCurrency(SaveCurrencyDto currency) {
        String userUUID = authenticationServiceUtil.getCurrentUserUuid();
        User user = userRepo.findById(userUUID)
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userUUID));
        Currency newCurrency = new Currency(user, currency.name(), currency.symbol());
        return currencyRepo.save(newCurrency);
    }

    public void deleteCurrency(Long id) {
        String userUUID = authenticationServiceUtil.getCurrentUserUuid();
        currencyRepo.deleteCurrencyByIdAndUserId(id,userUUID);
    }

    public boolean currencyExists(Long id) {
        return currencyRepo.existsById(id);
    }

    public Currency findByNameAndSymbolAndUserId(String name, String symbol, String userId) {
        return currencyRepo.findByNameAndSymbolAndUserId(name, symbol, userId)
                .orElse(null);
    }

}
