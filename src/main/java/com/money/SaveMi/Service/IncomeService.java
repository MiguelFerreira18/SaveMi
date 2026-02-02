package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Income.SaveIncomeDto;
import com.money.SaveMi.DTO.Income.UpdateIncomeDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Income;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.IncomeRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;
import org.springframework.stereotype.Service;

@Service
public class IncomeService {
    private IncomeRepo incomeRepo;
    private AuthenticationServiceUtil authUtil;
    private UserRepo userRepo;
    private CurrencyRepo currencyRepo;

    public IncomeService(IncomeRepo incomeRepo, AuthenticationServiceUtil authUtil, UserRepo userRepo, CurrencyRepo currencyRepo) {
        this.incomeRepo = incomeRepo;
        this.authUtil = authUtil;
        this.userRepo = userRepo;
        this.currencyRepo = currencyRepo;
    }

    //CRUD

    public Iterable<Income> getAllIncomeByUserId() {
        String userId = authUtil.getCurrentUserUuid();
        return incomeRepo.findAllIncomeByUserId(userId);
    }

    public Income getIncomeById(Long id) {
        String userId = authUtil.getCurrentUserUuid();
        return incomeRepo.findByIncomeIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Income not found with id: " + id + " for user: " + userId));
    }

    public Income saveIncome(SaveIncomeDto income) {
        String userId = authUtil.getCurrentUserUuid();
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userId));

        Currency currency = currencyRepo.findCurrencyByIdAndUserId(income.currencyId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Currency not found with id: " + income.currencyId() + " for user: " + userId));

        Income newIncome = new Income(user, currency, income.amount(), income.description(), income.date());

        return incomeRepo.save(newIncome);
    }

    public Income updateIncome(UpdateIncomeDto updatedIncomeDto) {
        String userId = authUtil.getCurrentUserUuid();

        Currency currency = currencyRepo.findCurrencyByIdAndUserId(updatedIncomeDto.currencyId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Currency not found with id: " + updatedIncomeDto.currencyId() + " for user: " + userId));

        Income oldIncome = incomeRepo.findByIncomeIdAndUserId(updatedIncomeDto.id(), userId)
                .orElseThrow(() -> new RuntimeException("Income not found with id: " + updatedIncomeDto.id() + " for user: " + userId));

        oldIncome.setAmount(updatedIncomeDto.amount());
        oldIncome.setDescription(updatedIncomeDto.description());
        oldIncome.setCurrency(currency);
        oldIncome.setDate(updatedIncomeDto.date());

        return incomeRepo.save(oldIncome);
    }

    public void bulkDelete(BulkDeleteDto bulkdeleteDto){
        String userId = authUtil.getCurrentUserUuid();

        bulkdeleteDto.ids().forEach(id -> {
            if(incomeRepo.findByIncomeIdAndUserId(id,userId).isEmpty()){
                throw new RuntimeException("Income not found with id: " + id + " for user: " + userId + " in bulk");
            }
        });

        incomeRepo.bulkDelete(bulkdeleteDto.ids(),userId);
    }

    public void deleteIncome(Long id) {
        String userId = authUtil.getCurrentUserUuid();

        if (incomeRepo.findByIncomeIdAndUserId(id, userId).isEmpty()) {
            throw new RuntimeException("Income not found with id: " + id + " for user: " + userId);
        }

        incomeRepo.deleteIncomeByIdAndUserId(id, userId);
    }


}
