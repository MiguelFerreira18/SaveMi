package com.money.SaveMi.Service;

import com.money.SaveMi.DTO.Investment.SaveInvestmentDto;
import com.money.SaveMi.DTO.Investment.UpdateInvestmentDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Investment;
import com.money.SaveMi.Model.StrategyType;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.InvestmentRepo;
import com.money.SaveMi.Repo.StrategyTypeRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Optional;

@Service
public class InvestmentService {

    private final InvestmentRepo investmentRepo;
    private final AuthenticationServiceUtil authUtil;
    private final CurrencyRepo currencyRepo;
    private final UserRepo userRepo;
    private final StrategyTypeRepo strategyTypeRepo;

    public InvestmentService(InvestmentRepo investmentRepo, AuthenticationServiceUtil authUtil, CurrencyRepo currencyRepo, UserRepo userRepo, StrategyTypeRepo strategyTypeRepo) {
        this.investmentRepo = investmentRepo;
        this.authUtil = authUtil;
        this.currencyRepo = currencyRepo;
        this.userRepo = userRepo;
        this.strategyTypeRepo = strategyTypeRepo;
    }

    public Iterable<Investment> getAllInvestments(Optional<YearMonth> month){
        String userId = authUtil.getCurrentUserUuid();
        return month.map( m -> {
            int monthValue = m.getMonthValue();
            int year = m.getYear();
            return investmentRepo.findAllByUserIdAndYearMonth(userId,year,monthValue);
        }).orElse(investmentRepo.findAllByUserId(userId));
    }

    public Investment getInvestmentById(Long id){
        String userId = authUtil.getCurrentUserUuid();
        return investmentRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Investment not found with id: " + id + " for user: " + userId));
    }

    public Investment saveInvestment(SaveInvestmentDto investmentDto) {
        String userId = authUtil.getCurrentUserUuid();
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userId));

        Currency currency = currencyRepo.findByIdAndUserId(investmentDto.currencyId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Currency not found with id: " + investmentDto.currencyId() + " for user: " + userId));

        StrategyType strategyType = strategyTypeRepo.findByIdAndUserId(investmentDto.strategyTypeId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Strategy type not found with id: " + investmentDto.strategyTypeId() + " for user: " + userId));

        Investment newInvestment = new Investment(user,currency,investmentDto.amount(), strategyType,investmentDto.description(),investmentDto.date());

        return investmentRepo.save(newInvestment);
    }

    public Investment updateInvestment(UpdateInvestmentDto updateInvestment) {
        String userId = authUtil.getCurrentUserUuid();

        Currency currency = currencyRepo.findByIdAndUserId(updateInvestment.currencyId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Currency not found with id: " + updateInvestment.currencyId() + " for user: " + userId));

        StrategyType strategyType = strategyTypeRepo.findByIdAndUserId(updateInvestment.strategyTypeId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Strategy type not found with id: " + updateInvestment.strategyTypeId() + " for user: " + userId));

        Investment oldInvestment = investmentRepo.findByIdAndUserId(updateInvestment.id(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Investment not found with id: " + updateInvestment.id() + " for user: " + userId));

        oldInvestment.setAmount(updateInvestment.amount());
        oldInvestment.setCurrency(currency);
        oldInvestment.setStrategyType(strategyType);
        oldInvestment.setDescription(updateInvestment.description());
        oldInvestment.setDate(updateInvestment.date());

        return investmentRepo.save(oldInvestment);
    }

    public void bulkDelete(BulkDeleteDto bulkdeleteDto){
        String userId = authUtil.getCurrentUserUuid();

        bulkdeleteDto.ids().forEach(id -> {
            if(investmentRepo.findByIdAndUserId(id,userId).isEmpty()){
                throw new RuntimeException("Investment not found with id: " + id + " for user: " + userId + " in bulk");
            }
        });

        investmentRepo.bulkDelete(bulkdeleteDto.ids(),userId);
    }

    public void deleteInvestment(Long id) {
        String userId = authUtil.getCurrentUserUuid();

        if(investmentRepo.findByIdAndUserId(id,userId).isEmpty()){
            throw new RuntimeException("Investment not found with id: " + id + " for user: " + userId);
        }

        investmentRepo.deleteByIdAndUserId(id, userId);
    }
}
