package com.money.SaveMi.Repo;

import com.money.SaveMi.Model.Wish;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface WishRepo extends CrudRepository<Wish, Long> {

    @Query("SELECT w from Wish w WHERE w.user.id = ?1")
    Iterable<Wish> findAllByUserId(String userUUID);

    @Query("SELECT w from Wish w where w.user.id = ?1 and year(w.date) = ?2 and month(w.date) = ?3")
    Iterable<Wish> findAllByUserIdFilteredByMonthAndYear(String userUUID, int year, int month);

    @Query("SELECT w from Wish w WHERE w.user.id = ?1 AND w.currency.id = ?2")
    Optional<Wish> findByUserIdAndCurrencyId(String userUUID, Long currencyId);

    @Query("SELECT w from Wish w WHERE w.id = ?1 AND w.user.id = ?2")
    Optional<Wish> findByIdAndUserId(Long id, String userUUID);

    @Query("SELECT w from Wish w WHERE w.user.id = ?1 AND w.currency.id = ?2 AND w.amount = ?3")
    Optional<Wish> findByUserIdCurrencyIdAndAmount(String userId, Long currencyId, BigDecimal amount);

    @Modifying
    @Transactional
    @Query("DELETE FROM Wish w WHERE w.id in ?1 AND w.user.id = ?2")
    void bulkDelete(List<Long> ids, String userUUID);

    @Modifying
    @Transactional
    @Query("DELETE FROM Wish w WHERE w.id = ?1 AND w.user.id = ?2")
    void deleteByIdAndUserId(Long id, String userUUID);
}
