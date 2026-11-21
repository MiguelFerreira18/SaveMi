package com.money.SaveMi.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.money.SaveMi.DTO.Investment.SaveInvestmentDto;
import com.money.SaveMi.DTO.Investment.UpdateInvestmentDto;
import com.money.SaveMi.DTO.Shared.BulkDeleteDto;
import com.money.SaveMi.Filter.InvestmentFilter;
import com.money.SaveMi.Model.Currency;
import com.money.SaveMi.Model.Investment;
import com.money.SaveMi.Model.StrategyType;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.CurrencyRepo;
import com.money.SaveMi.Repo.InvestmentRepo;
import com.money.SaveMi.Repo.StrategyTypeRepo;
import com.money.SaveMi.Repo.UserRepo;
import com.money.SaveMi.Utils.AuthenticationServiceUtil;

@Service
public class InvestmentService {

    private final InvestmentRepo investmentRepo;
    private final AuthenticationServiceUtil authUtil;
    private final CurrencyRepo currencyRepo;
    private final UserRepo userRepo;
    private final StrategyTypeRepo strategyTypeRepo;

    public InvestmentService(InvestmentRepo investmentRepo, AuthenticationServiceUtil authUtil,
            CurrencyRepo currencyRepo, UserRepo userRepo, StrategyTypeRepo strategyTypeRepo) {
        this.investmentRepo = investmentRepo;
        this.authUtil = authUtil;
        this.currencyRepo = currencyRepo;
        this.userRepo = userRepo;
        this.strategyTypeRepo = strategyTypeRepo;
    }

    public Page<Investment> getAllInvestments(InvestmentFilter filter, Pageable pageable) {
        List<Specification<Investment>> specs = new ArrayList<>();
        String userId = authUtil.getCurrentUserUuid();

        specs.add((root, q, cb) -> cb.equal(root.get("user").get("id"), userId));

        if (filter.text() != null && !filter.text().isBlank()) {
            String like = "%" + filter.text().toLowerCase() + "%";
            specs.add((root, q, cb) -> {
                return cb.or(
                        cb.like(cb.lower(root.get("strategyType").get("name")), like),
                        cb.like(cb.lower(root.get("currency").get("name")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(root.get("amount").as(String.class), like));
            });
        }
        if (filter.strategy() != null) {
            specs.add((root, q, cb) -> cb.equal(root.get("strategyType").get("id"), filter.strategy()));
        }
        if (filter.symbol() != null) {
            specs.add((root, q, cb) -> cb.equal(root.get("currency").get("id"), filter.symbol()));
        }
        if (filter.amountStart() != null) {
            specs.add((root, q, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), filter.amountStart()));
        }
        if (filter.amountFinish() != null) {
            specs.add((root, q, cb) -> cb.lessThanOrEqualTo(root.get("amount"), filter.amountFinish()));
        }
        return investmentRepo.findAll(Specification.allOf(specs), pageable);
    }

    public Investment getInvestmentById(Long id) {
        String userId = authUtil.getCurrentUserUuid();
        return investmentRepo.findByIdAndUserId(id, userId)
                .orElseThrow(
                        () -> new RuntimeException("Investment not found with id: " + id + " for user: " + userId));
    }

    public Investment saveInvestment(SaveInvestmentDto investmentDto) {
        String userId = authUtil.getCurrentUserUuid();
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userId));

        Currency currency = currencyRepo.findByIdAndUserId(investmentDto.currencyId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Currency not found with id: " + investmentDto.currencyId() + " for user: " + userId));

        StrategyType strategyType = strategyTypeRepo.findByIdAndUserId(investmentDto.strategyTypeId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Strategy type not found with id: " + investmentDto.strategyTypeId() + " for user: " + userId));

        Investment newInvestment = new Investment(user, currency, investmentDto.amount(), strategyType,
                investmentDto.description(), investmentDto.date());

        return investmentRepo.save(newInvestment);
    }

    public Investment updateInvestment(UpdateInvestmentDto updateInvestment) {
        String userId = authUtil.getCurrentUserUuid();

        Currency currency = currencyRepo.findByIdAndUserId(updateInvestment.currencyId(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Currency not found with id: " + updateInvestment.currencyId() + " for user: " + userId));

        StrategyType strategyType = strategyTypeRepo.findByIdAndUserId(updateInvestment.strategyTypeId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Strategy type not found with id: "
                        + updateInvestment.strategyTypeId() + " for user: " + userId));

        Investment oldInvestment = investmentRepo.findByIdAndUserId(updateInvestment.id(), userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Investment not found with id: " + updateInvestment.id() + " for user: " + userId));

        oldInvestment.setAmount(updateInvestment.amount());
        oldInvestment.setCurrency(currency);
        oldInvestment.setStrategyType(strategyType);
        oldInvestment.setDescription(updateInvestment.description());
        oldInvestment.setDate(updateInvestment.date());

        return investmentRepo.save(oldInvestment);
    }

    public void bulkDelete(BulkDeleteDto bulkdeleteDto) {
        String userId = authUtil.getCurrentUserUuid();

        bulkdeleteDto.ids().forEach(id -> {
            if (investmentRepo.findByIdAndUserId(id, userId).isEmpty()) {
                throw new RuntimeException("Investment not found with id: " + id + " for user: " + userId + " in bulk");
            }
        });

        investmentRepo.bulkDelete(bulkdeleteDto.ids(), userId);
    }

    public void deleteInvestment(Long id) {
        String userId = authUtil.getCurrentUserUuid();

        if (investmentRepo.findByIdAndUserId(id, userId).isEmpty()) {
            throw new RuntimeException("Investment not found with id: " + id + " for user: " + userId);
        }

        investmentRepo.deleteByIdAndUserId(id, userId);
    }
}
