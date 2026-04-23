package com.money.SaveMi.Repo;

import com.money.SaveMi.Model.Objective;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ObjectiveRepo extends CrudRepository<Objective, Long> {

    @Query("SELECT o FROM Objective o WHERE o.user.id = ?1")
    Iterable<Objective> findAllByUserId(String userUUID);

    @Query("SELECT o FROM Objective o WHERE o.id = ?1 AND o.user.id = ?2")
    Optional<Objective> findByIdAndUserId(Long id, String userUUID);

    @Query("SELECT o FROM Objective o WHERE o.user.id = ?1 AND o.currency.id = ?2 AND o.amount = ?3 AND o.target = ?4")
    Optional<Objective> findByUserIdAndCurrencyIdAndAmountAndYear(String userUUID, Long currencyId, BigDecimal amount, int year);

    @Modifying
    @Transactional
    @Query("DELETE FROM Objective o WHERE o.id IN ?1 AND o.user.id = ?2")
    void bulkDelete(List<Long> ids, String userUUID);

    @Transactional
    @Modifying
    @Query("DELETE FROM Objective o WHERE o.id = ?1 AND o.user.id = ?2")
    void deleteByIdAndUserId(Long id, String userUUID);
}
