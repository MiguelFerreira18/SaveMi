package com.money.SaveMi.init;

import com.money.SaveMi.Model.User;
import com.money.SaveMi.Service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(BootstrapOrder.USERS)
@Profile("dev")
public class UserBootstrap implements CommandLineRunner {
    private final UserService userService;
    private final PasswordEncoder encoder;

    public UserBootstrap(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        User u1 = new User("coisa@gmail.com","Coisa","Password1!");
        u1.setPassword(encoder.encode(u1.getPassword()));
        if (userService.getUserByEmail(u1.getEmail()) == null) {
            userService.saveUser(u1);
        }
        User u2 = new User("coisa2@gmail.com","Coisa2","Password2!");
        u2.setPassword(encoder.encode(u2.getPassword()));
        if (userService.getUserByEmail(u2.getEmail()) == null) {
            userService.saveUser(u2);
        }
    }
}
