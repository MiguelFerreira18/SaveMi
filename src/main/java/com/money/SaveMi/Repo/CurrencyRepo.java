package com.money.SaveMi.Repo;

import com.money.SaveMi.Model.Currency;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepo extends CrudRepository<Currency, Long> {

    @Query("SELECT c FROM Currency c WHERE c.user.id = ?1")
    Iterable<Currency> findAllCurrenciesByUserId(String userUUID);

    @Query("SELECT c FROM Currency c WHERE c.id = ?1 AND c.user.id = ?2")
    Optional<Currency> findCurrencyByIdAndUserId(Long id, String userUUID);

    @Query("SELECT c FROM Currency c WHERE c.name = ?1 AND c.symbol = ?2 AND c.user.id = ?3")
    Optional<Currency> findByNameAndSymbolAndUserId(String name, String symbol, String userUUID);

    @Modifying
    @Transactional
    @Query("DELETE FROM Currency c WHERE c.id in ?1 AND c.user.id = ?2")
    void bulkDelete(List<Long> ids, String userUUID);

    @Transactional
    @Modifying
    @Query("DELETE FROM Currency c WHERE c.id = ?1 AND c.user.id = ?2")
    void deleteCurrencyByIdAndUserId(Long id, String userUUID);

}
