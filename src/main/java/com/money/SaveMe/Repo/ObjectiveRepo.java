package com.money.SaveMe.Repo;

import com.money.SaveMe.Model.Objective;
import com.money.SaveMe.Model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ObjectiveRepo extends CrudRepository<Objective, Long> {

    @Query("SELECT o FROM Objective o WHERE o.user.id = ?1")
    public Iterable<Objective> findAllObjectiveByUserId(String userUUID);

    @Query("SELECT o FROM Objective o WHERE o.user.id = ?1 AND o.currency.id = ?2")
    public Iterable<Objective> findAllObjectiveByUserIdFromCurrency(String userUUID, Long currencyId);

    @Query("SELECT o FROM Objective o WHERE o.id = ?1 AND o.user.id = ?2")
    public Optional<Objective> findByObjectiveIdAndUserId(Long id, String userUUID);

    @Query("SELECT o FROM Objective o WHERE o.user.id = ?1 AND o.currency.id = ?2 AND o.amount = ?3 AND o.target = ?4")
    public Optional<Objective> findObjectiveByAllFields(String userId, Long currencyId, BigDecimal amount, int year);

    @Transactional
    @Modifying
    @Query("DELETE FROM Objective o WHERE o.id = ?1 AND o.user.id = ?2")
    public void deleteObjectiveByIdAndUserId(Long id, String userUUID);


    String user(User user);
}
