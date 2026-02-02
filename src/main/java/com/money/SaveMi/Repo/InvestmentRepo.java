package com.money.SaveMi.Repo;

import com.money.SaveMi.Model.Investment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRepo extends CrudRepository<Investment, Long> {
    @Query("SELECT i FROM Investment i WHERE i.user.id = ?1")
    Iterable<Investment> findAllInvestmentsByUserId(String userUUID);

    @Query("SELECT i FROM Investment i WHERE i.user.id = ?1 AND i.currency.id = ?2")
    Iterable<Investment> findAllInvestmentByUserIdFromCurrency(String userUUID, Long currencyId);

    @Query("SELECT i FROM Investment i WHERE i.id = ?1 AND i.user.id = ?2")
    Optional<Investment> findByInvestmentIdAndUserId(Long id, String userUUID);

    @Query("SELECT i FROM Investment i WHERE i.user.id = ?1 AND i.strategyType.id = ?2 AND i.currency.id = ?3 AND i.amount = ?4")
    Optional<Investment> findInvestmentByParameters(String userUUID, Long strategyTypeId, Long currencyId, BigDecimal amount);

    @Modifying
    @Transactional
    @Query("DELETE FROM Investment i WHERE i.id in ?1 AND i.user.id = ?2")
    void bulkDelete(List<Long> ids, String userUUID);

    @Transactional
    @Modifying
    @Query("DELETE FROM Investment i WHERE i.id = ?1 AND i.user.id = ?2")
    void deleteInvestmentByIdAndUserId(Long id, String userUUID);
}
