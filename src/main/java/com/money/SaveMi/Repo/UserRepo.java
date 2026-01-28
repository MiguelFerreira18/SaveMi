package com.money.SaveMi.Repo;

import com.money.SaveMi.Model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepo extends CrudRepository<User,String> {

    @Query("SELECT u FROM User u where u.email = ?1")
     Optional<User> findByEmail(String email);
}
