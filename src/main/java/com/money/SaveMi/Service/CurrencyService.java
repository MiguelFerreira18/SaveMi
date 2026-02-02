package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Currency.SaveCurrencyDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
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
    private final AuthenticationServiceUtil authUtil;

    public CurrencyService(CurrencyRepo currencyRepo, UserRepo userRepo, AuthenticationServiceUtil authUtil) {
        this.currencyRepo = currencyRepo;
        this.userRepo = userRepo;
        this.authUtil = authUtil;
    }

    public Iterable<Currency> getAllCurrenciesFromUser() {
        String userUUID = authUtil.getCurrentUserUuid();
        return currencyRepo.findAllCurrenciesByUserId(userUUID);
    }

    public Currency getCurrencyById(Long id) {
        String userUUID = authUtil.getCurrentUserUuid();
        return currencyRepo.findCurrencyByIdAndUserId(id, userUUID)
                .orElseThrow(() -> new RuntimeException("Currency not found with id: " + id + " for user: " + userUUID));
    }

    public Currency saveCurrency(SaveCurrencyDto currency) {
        String userUUID = authUtil.getCurrentUserUuid();
        User user = userRepo.findById(userUUID)
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userUUID));
        Currency newCurrency = new Currency(user, currency.name(), currency.symbol());
        return currencyRepo.save(newCurrency);
    }

    public void bulkDelete(BulkDeleteDto bulkdeleteDto){
        String userId = authUtil.getCurrentUserUuid();

        bulkdeleteDto.ids().forEach(id -> {
            if(currencyRepo.findCurrencyByIdAndUserId(id,userId).isEmpty()){
                throw new RuntimeException("Currency not found with id: " + id + " for user: " + userId + " in bulk");
            }
        });

        currencyRepo.bulkDelete(bulkdeleteDto.ids(),userId);
    }

    public void deleteCurrency(Long id) {
        String userUUID = authUtil.getCurrentUserUuid();

        if(currencyRepo.findCurrencyByIdAndUserId(id,userUUID).isEmpty()){
            throw new RuntimeException("Currency not found with id: " + id + " for user: " + userUUID);
        }

        currencyRepo.deleteCurrencyByIdAndUserId(id,userUUID);
    }

    public Currency findByNameAndSymbolAndUserId(String name, String symbol, String userId) {
        return currencyRepo.findByNameAndSymbolAndUserId(name, symbol, userId)
                .orElse(null);
    }

}
