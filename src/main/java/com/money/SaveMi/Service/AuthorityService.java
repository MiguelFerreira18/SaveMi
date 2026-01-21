package com.money.SaveMi.Service;

import com.money.SaveMi.Model.Authority;
import com.money.SaveMi.Repo.AuthorityRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityService {
    private final AuthorityRepo authorityRepo;

    public AuthorityService(AuthorityRepo authorityRepo) {
        this.authorityRepo = authorityRepo;
    }


    public void saveAuthority(Authority authority) {
        authorityRepo.save(authority);
    }

    public boolean authorityExists(Authority.Role role) {
        boolean exists = false;
        List<Authority> authorities = (List<Authority>) authorityRepo.findAll();
        for (Authority authority : authorities) {
            if (authority.getAuthority().equals(role.name())) {
                exists = true;
                break;
            }
        }
        return exists;
    }
}
