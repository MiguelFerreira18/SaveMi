package com.money.SaveMi.Repo;

import com.money.SaveMi.Model.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CategoryRepo extends CrudRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.user.id = ?1")
    Iterable<Category> findAllCategoriesByUserId(String userUUID);

    @Query("SELECT c FROM Category c WHERE c.id = ?1 AND c.user.id = ?2")
    Optional<Category> findCategoryByIdAndUserId(Long id, String userUUID);

    @Query("SELECT c FROM Category c WHERE c.name = ?1 AND c.description = ?2 AND c.user.id = ?3")
    Optional<Category> findByNameDescriptionAndUserId(String name, String description, String userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Category c WHERE c.id = ?1 AND c.user.id = ?2")
    void deleteById(Long id, String userUUID);


}
