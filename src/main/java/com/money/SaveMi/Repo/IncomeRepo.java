package com.money.SaveMi.Repo;

import com.money.SaveMi.Model.Income;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepo extends CrudRepository<Income, Long> {

    @Query("SELECT i FROM Income i WHERE i.user.id = ?1")
    Iterable<Income> findAllIncomeByUserId(String userUUID);

    @Query("SELECT i FROM Income i WHERE i.user.id = ?1 AND i.currency.id = ?2")
    Iterable<Income> findAllIncomeByUserIdFromCurrency(String userUUID, Long currencyId);

    @Query("SELECT i FROM Income i WHERE i.id = ?1 AND i.user.id = ?2")
    Optional<Income> findByIncomeIdAndUserId(Long id, String userUUID);

    @Query("SELECT i FROM Income i WHERE i.user.id = ?1 AND i.currency.id = ?2 AND i.amount = ?3")
    Optional<Income> findIncomeByUserIdCurrencyAndAmount(String userUUID, Long currencyId, BigDecimal amount);

    @Modifying
    @Transactional
    @Query("DELETE FROM Income i WHERE i.id in ?1 AND i.user.id = ?2")
    void bulkDelete(List<Long> ids, String userUUID);

    @Transactional
    @Modifying
    @Query("DELETE FROM Income i WHERE i.id = ?1 AND i.user.id = ?2")
    void deleteIncomeByIdAndUserId(Long id, String userUUID);

}
