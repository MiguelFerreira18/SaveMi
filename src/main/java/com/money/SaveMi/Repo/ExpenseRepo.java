package com.money.SaveMi.Repo;

import com.money.SaveMi.Model.Expense;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepo extends CrudRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE e.user.id = ?1")
    Iterable<Expense> findAllExpensesByUserId(String userUUID);

    @Query("SELECT e FROM Expense e WHERE e.user.id = ?1 AND e.currency.id = ?2")
    Iterable<Expense> findAllIncomeByUserIdFromCurrency(String userUUID, Long currencyId);

    @Query("SELECT e FROM Expense e WHERE e.id = ?1 AND e.user.id = ?2")
    Optional<Expense> findByExpenseIdAndUserId(Long id, String userUUID);

    @Query("SELECT e FROM Expense e WHERE e.user.id = ?1 AND e.currency.id = ?2 AND e.category.id = ?3 AND e.description = ?4 AND e.amount = ?5")
    Optional<Expense> findExpenseByUserIdCurrencyCategoryAndAmount(String userUUID, Long currencyId, Long categoryId, String description, BigDecimal amount);

    @Modifying
    @Transactional
    @Query("DELETE FROM Expense e WHERE e.id in ?1 AND e.user.id = ?2")
    void bulkDelete(List<Long> ids, String userUUID);

    @Transactional
    @Modifying
    @Query("DELETE FROM Expense e WHERE e.id = ?1 AND e.user.id = ?2")
    void deleteExpenseByIdAndUserId(Long id, String userUUID);
}
