package com.money.SaveMi.Repo;

import com.money.SaveMi.Model.StrategyType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StrategyTypeRepo extends CrudRepository<StrategyType, Long> {

    @Query("SELECT s FROM StrategyType s WHERE s.user.id = ?1")
    Iterable<StrategyType> findAllByUserId(String userUUID);

    @Query("SELECT s FROM StrategyType s WHERE s.id = ?1 AND s.user.id = ?2")
    Optional<StrategyType> findStrategyTypeByIdAndUserId(Long id, String userUUID);

    @Query("SELECT s FROM StrategyType s WHERE s.name = ?1 AND s.description = ?2 AND s.user.id = ?3")
    Optional<StrategyType> findByNameDescriptionAndUserId(String name, String description, String userUUID);

    @Modifying
    @Transactional
    @Query("DELETE FROM StrategyType s WHERE s.id in ?1 AND s.user.id = ?2")
    void bulkDelete(List<Long> ids, String userUUID);

    @Transactional
    @Modifying
    @Query("DELETE FROM StrategyType s WHERE s.id = ?1 AND s.user.id = ?2")
    void deleteById(Long id, String userUUID);
}
