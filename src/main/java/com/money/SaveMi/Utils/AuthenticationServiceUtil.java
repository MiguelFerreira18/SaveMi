package com.money.SaveMi.Utils;

import com.money.SaveMi.Model.User;
import com.money.SaveMi.Repo.UserRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationServiceUtil {

    private final UserRepo userRepo;

    public AuthenticationServiceUtil(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public String getCurrentUserUuid(){
        return getJwtToken()
                .map(jwt -> jwt.getClaimAsString("uuid"))
                .filter(uui -> uui != null && !uui.isEmpty())
                .orElseThrow(() -> new RuntimeException("User UUID not found in token"));
    }

    public String getCurrentUserEmail(){
        return getJwtToken()
                .map(jwt -> jwt.getClaimAsString("email"))
                .filter(email -> email != null && !email.isEmpty())
                .orElseThrow(() -> new RuntimeException("User email not found in token"));
    }

    public User getCurrentUser() {
        String userUuid = getCurrentUserUuid();
        return userRepo.findById(userUuid)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));
    }

    public boolean isResourceOwner(String resourceUserId) {
        try {
            String currentUserUuid = getCurrentUserUuid();
            return currentUserUuid.equals(resourceUserId);
        } catch (Exception e) {
            return false;
        }
    }


    private Optional<Jwt> getJwtToken(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken){
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            return Optional.of(jwtAuth.getToken());
        }

        return Optional.empty();
    }

    public Optional<Jwt> getCurrentJwtToken() {
        return getJwtToken();
    }

}
