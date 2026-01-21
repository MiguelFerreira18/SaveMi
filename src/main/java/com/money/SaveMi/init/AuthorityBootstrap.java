package com.money.SaveMi.init;

import com.money.SaveMi.Model.Authority;
import com.money.SaveMi.Service.AuthorityService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(BootstrapOrder.AUTHORITIES)
public class AuthorityBootstrap implements CommandLineRunner {
    private final AuthorityService authorityService;

    public AuthorityBootstrap(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!authorityService.authorityExists(Authority.Role.USER)) {
            authorityService.saveAuthority(new Authority(Authority.Role.USER));
        }
        if (!authorityService.authorityExists(Authority.Role.ADMIN)) {
            authorityService.saveAuthority(new Authority(Authority.Role.ADMIN));
        }
    }
}
