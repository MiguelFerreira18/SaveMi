package com.money.SaveMi.Config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {
    private static final String JWT_COOKIE_NAME = "jwt-token";

    @Override
    public String resolve(HttpServletRequest request){

        Cookie[] cookies = request.getCookies();
        if (cookies != null ){
            for (Cookie cookie : cookies){
                if (JWT_COOKIE_NAME.equals(cookie.getName())){
                    String token = cookie.getValue();
                    return (token != null && !token.isEmpty()) ? token : null;
                }
            }
        }

        return null;
    }
}
