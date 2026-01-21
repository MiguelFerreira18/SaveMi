package com.money.SaveMi.Service;

import com.money.SaveMi.Model.Authority;
import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.AuthorityRepo;
import com.money.SaveMi.Repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {


    private final UserRepo userRepo;
    private final AuthorityRepo authorityRepo;

    public UserService(UserRepo userRepo, AuthorityRepo authorityRepo) {
        this.userRepo = userRepo;
        this.authorityRepo = authorityRepo;
    }

    public Optional<User> saveUser(User user) {

        Optional<Authority> authorityOpt = authorityRepo.findByAuthority(Authority.Role.USER);
        if (authorityOpt.isEmpty()) {
            return Optional.empty();
        }
        Optional<User> userOptional;
        try {
            user.setPassword(user.getPassword());
            user.addAuthority(authorityOpt.get());
            userOptional = Optional.of(userRepo.save(user));
        } catch (Exception e) {
            return Optional.empty();
        }
        return userOptional;
    }

    @Transactional
    public User getUserByEmail(String email) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        return userOpt.orElse(null);
    }


    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UsernameNotFoundException("User not found with email: " + email);
    }

}
